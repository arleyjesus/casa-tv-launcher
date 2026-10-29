package tv.casa.launcher;

import android.media.MediaDataSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;

/** Two bounded windows; a session never joins different HTTP representations. */
final class RangeMediaSource extends MediaDataSource {
    private static final int CHUNK=512*1024;
    private final String url;private final long budget;private final AmbientHttp http;
    private final LinkedHashMap<Long,byte[]> cache=new LinkedHashMap<>(3,.75f,true);
    private InputStream input;private volatile HttpURLConnection connection;
    private long cursor=-1,length=-1,responseEnd=-1;private String identity;private boolean opened;
    private volatile long readBytes;private volatile boolean closed;private volatile IOException failure;
    interface Trace {void event(String metadata);}
    private Trace trace;void trace(Trace listener){trace=listener;}
    private void emit(String metadata){if(trace!=null)trace.event(metadata);}
    String failureKind(){IOException e=failure;if(e==null)return closed?"STOPPED":"NONE";String message=String.valueOf(e.getMessage());if(message.contains("Límite"))return "QUOTA";if(e instanceof EOFException)return "TRUNCATED";if(message.contains("cambió")||message.contains("ETag")||message.contains("modificada"))return "IDENTITY";if(e instanceof javax.net.ssl.SSLException||e.getCause() instanceof javax.net.ssl.SSLException)return "TLS";return "NETWORK_OR_RANGE";}
    RangeMediaSource(String address,long budget){this(address,budget,new AmbientHttp());}
    RangeMediaSource(String address,long budget,AmbientHttp client){if(!OnlinePolicy.https(address)||budget<1)throw new IllegalArgumentException();url=address;this.budget=budget;http=client;}
    private void at(long position)throws IOException{
        if(closed)throw new InterruptedIOException("Reproducción detenida");
        if(input!=null&&position==cursor&&position<=responseEnd)return;
        if(opened&&identity==null)throw new IOException("El servidor no ofrece ETag fuerte para continuar o buscar");
        disconnect();
        try{
            long began=System.nanoTime();connection=http.open(url,"bytes="+position+"-",identity);if(closed)throw new InterruptedIOException("Detenido");
            int status=connection.getResponseCode();long total,end;
            if(status==206){long[] range=RangePolicy.bounds(connection.getHeaderField("Content-Range"),position);end=range[1];total=range[2];long declared=connection.getContentLengthLong();if(declared>=0&&declared!=end-position+1)throw new IOException("Longitud del rango incoherente");}
            else if(status==200&&position==0&&!opened){total=connection.getContentLengthLong();end=total-1;}
            else throw new IOException("Respuesta HTTP no compatible con el rango: "+status);
            if(total<1||total>8L*1024*1024*1024||length>=0&&length!=total)throw new IOException("Longitud de vídeo no compatible o modificada");
            String tag=connection.getHeaderField("ETag");boolean strong=RangePolicy.strongTag(tag);
            if(opened&&!identity.equals(tag))throw new IOException("El vídeo cambió durante la sesión");
            if(!opened)identity=strong?tag:null;
            String encoding=connection.getHeaderField("Content-Encoding");if(encoding!=null&&!encoding.equalsIgnoreCase("identity"))throw new IOException("Rango con codificación no compatible");
            opened=true;length=total;responseEnd=end;input=connection.getInputStream();cursor=position;emit("http="+status+" range="+position+"-"+end+" total="+total+" validated="+(identity!=null)+" ms="+((System.nanoTime()-began)/1000000));
        }catch(IOException e){disconnect();throw e;}catch(Exception e){disconnect();throw new IOException("No se pudo abrir el vídeo",e);}
    }
    private byte[] window(long base)throws IOException{
        byte[] data=cache.get(base);if(data!=null)return data;
        if(readBytes>=budget)throw new IOException("Límite de datos alcanzado");
        at(base);int wanted=(int)Math.min(CHUNK,Math.min(length-base,budget-readBytes));
        if(wanted<1)throw new IOException("No quedan datos disponibles");
        byte[] block=new byte[wanted];int count=0,parts=0;
        while(count<wanted){
            if(closed)throw new InterruptedIOException("Detenido");
            if(cursor>responseEnd){if(++parts>32)throw new IOException("Demasiados rangos parciales");at(cursor);}
            int allowed=(int)Math.min(wanted-count,responseEnd-cursor+1);
            int n=input.read(block,count,allowed);
            if(n<0)throw new EOFException("Cuerpo HTTP truncado antes del fin declarado");
            if(n==0)throw new IOException("Lectura HTTP sin progreso");
            count+=n;cursor+=n;readBytes+=n;
        }
        if(closed)throw new InterruptedIOException("Detenido");
        if(cache.size()>=2)cache.remove(cache.keySet().iterator().next());cache.put(base,block);return block;
    }
    public synchronized int readAt(long position,byte[] buffer,int offset,int size)throws IOException{
        if(closed)throw new InterruptedIOException("Detenido");if(failure!=null)throw failure;
        if(position<0||offset<0||size<0||offset>buffer.length-size)throw new IOException("Lectura no válida");if(size==0)return 0;
        try{if(length<0)at(0);if(position>=length)return -1;int copied=0;
            while(copied<size&&position+copied<length){long next=position+copied,base=next/CHUNK*CHUNK;byte[] data=window(base);int within=(int)(next-base);if(within>=data.length)throw new IOException("Límite de datos alcanzado");int count=Math.min(size-copied,data.length-within);System.arraycopy(data,within,buffer,offset+copied,count);copied+=count;}
            return copied;
        }catch(IOException e){failure=e;emit("transport="+failureKind());disconnect();throw e;}
    }
    public synchronized long getSize()throws IOException{if(closed)throw new InterruptedIOException("Detenido");if(failure!=null)throw failure;try{if(length<0)at(0);return length;}catch(IOException e){failure=e;disconnect();throw e;}}
    long bytes(){return readBytes;}
    synchronized void clearCache(){cache.clear();}
    private void disconnect(){HttpURLConnection c=connection;connection=null;if(c!=null)c.disconnect();try{if(input!=null)input.close();}catch(Exception ignored){}input=null;cursor=-1;}
    void requestClose(){closed=true;http.cancelled=true;}
    public void close(){requestClose();http.cancel();HttpURLConnection c=connection;if(c!=null)c.disconnect();}
}
