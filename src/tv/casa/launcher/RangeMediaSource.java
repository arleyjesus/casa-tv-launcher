package tv.casa.launcher;

import android.media.MediaDataSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.*;

/** Two 512 KiB windows avoid a network request for every small MP4 header read. */
final class RangeMediaSource extends MediaDataSource {
    private static final int CHUNK=512*1024;
    private final String url;private final long budget;private final AmbientHttp http;
    private final LinkedHashMap<Long,byte[]> cache=new LinkedHashMap<>(3,.75f,true);
    private InputStream input;private HttpURLConnection connection;private long cursor=-1,length=-1;private volatile long readBytes;private volatile boolean closed;
    RangeMediaSource(String address,long budget){this(address,budget,new AmbientHttp());}
    RangeMediaSource(String address,long budget,AmbientHttp client){if(!OnlinePolicy.https(address))throw new IllegalArgumentException();url=address;this.budget=budget;http=client;}
    private void at(long position)throws IOException{
        if(closed)throw new IOException("Reproducción detenida");if(input!=null&&position==cursor)return;disconnect();
        try{connection=http.open(url,"bytes="+position+"-",null);int status=connection.getResponseCode();
            if(status==206){long total=RangePolicy.length(connection.getHeaderField("Content-Range"),position);if(length>=0&&length!=total)throw new IOException("El vídeo cambió durante la sesión");length=total;}
            else if(status==200&&position==0){length=connection.getContentLengthLong();}
            else throw new IOException("El servidor no permite buscar en el vídeo");
            if(length<1||length>8L*1024*1024*1024)throw new IOException("Longitud de vídeo no compatible");input=connection.getInputStream();cursor=position;
        }catch(IOException e){disconnect();throw e;}catch(Exception e){disconnect();throw new IOException("No se pudo abrir el vídeo",e);}
    }
    private byte[] window(long base)throws IOException{
        byte[] data=cache.get(base);if(data!=null)return data;if(readBytes>=budget)throw new IOException("Límite de datos alcanzado");at(base);int wanted=(int)Math.min(CHUNK,Math.min(length-base,budget-readBytes));if(wanted<1)return new byte[0];if(cache.size()>=2)cache.remove(cache.keySet().iterator().next());byte[] block=new byte[wanted];int count=0;
        while(count<wanted){if(closed)throw new IOException("Detenido");int n=input.read(block,count,wanted-count);if(n<0)break;count+=n;cursor+=n;readBytes+=n;}if(closed)throw new IOException("Detenido");data=count==block.length?block:Arrays.copyOf(block,count);cache.put(base,data);return data;
    }
    public synchronized int readAt(long position,byte[] buffer,int offset,int size)throws IOException{
        if(closed)throw new IOException("Detenido");if(size==0)return 0;if(position<0||offset<0||size<0||offset>buffer.length-size)throw new IOException("Lectura no válida");if(length<0)at(0);if(position>=length)return -1;int copied=0;
        while(copied<size&&position+copied<length){long at=position+copied,base=at/CHUNK*CHUNK;byte[] data=window(base);int within=(int)(at-base);if(within>=data.length){if(readBytes>=budget)throw new IOException("Límite de datos alcanzado");break;}int count=Math.min(size-copied,data.length-within);System.arraycopy(data,within,buffer,offset+copied,count);copied+=count;}
        return copied==0?-1:copied;
    }
    public synchronized long getSize()throws IOException{if(length<0)at(0);return length;}
    long bytes(){return readBytes;}
    synchronized void clearCache(){cache.clear();}
    private void disconnect(){try{if(input!=null)input.close();}catch(Exception ignored){}input=null;if(connection!=null)connection.disconnect();connection=null;cursor=-1;}
    public void close(){closed=true;http.cancel();HttpURLConnection c=connection;if(c!=null)c.disconnect();}
}
