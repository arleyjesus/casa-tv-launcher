package tv.casa.launcher;
import java.io.*;
import java.net.*;
public final class RangeMediaSourceTest {
    static void check(boolean value){if(!value)throw new AssertionError();}
    interface IO {void run()throws Exception;}
    static void fails(String label,IO task)throws Exception{try{task.run();throw new AssertionError(label+" was accepted");}catch(IOException expected){}}
    static final class Server extends AmbientHttp {
        final byte[] content;int requests,part=Integer.MAX_VALUE,truncate=Integer.MAX_VALUE,secondStatus=206;boolean changeTag,noTag,badRange,badLength,tls;String lastCondition;
        Server(){this(2*1024*1024);}Server(int size){content=new byte[size];for(int i=0;i<content.length;i++)content[i]=(byte)(i%251);}
        HttpURLConnection open(String address,String range,String tag)throws Exception{
            requests++;lastCondition=tag;if(tls)throw new javax.net.ssl.SSLHandshakeException("test certificate rejected");
            final int start=Integer.parseInt(range.substring(6,range.length()-1)),end=(int)Math.min(content.length-1L,start+(long)part-1),sent=Math.min(end-start+1,truncate),status=requests==1?206:secondStatus;
            final String etag=noTag?null:changeTag&&requests>1?"\"v2\"":"\"v1\"";
            return new HttpURLConnection(new URL(address)){
                public int getResponseCode(){return status;}public long getContentLengthLong(){return badLength?end-start+2:end-start+1;}
                public String getHeaderField(String name){if(name.equals("ETag"))return etag;return name.equals("Content-Range")?"bytes "+(badRange?start+1:start)+"-"+end+"/"+content.length:null;}
                public InputStream getInputStream(){return new ByteArrayInputStream(content,start,sent);}public void disconnect(){}public boolean usingProxy(){return false;}public void connect(){}
            };
        }
        void cancel(){cancelled=true;}
    }
    static RangeMediaSource reader(Server server,long budget){return new RangeMediaSource("https://test.invalid/v.mp4",budget,server);}
    public static void main(String[] args)throws Exception{
        byte[] buffer=new byte[16];
        check(RangePolicy.strongTag("\"v1\""));check(RangePolicy.strongTag("\"\""));check(!RangePolicy.strongTag("W/\"v1\""));check(!RangePolicy.strongTag("\"one\",\"two\""));check(!RangePolicy.strongTag("\"bad\n\""));
        Server truncated=new Server(1000);truncated.truncate=100;RangeMediaSource broken=reader(truncated,10000);
        fails("truncated body converted to EOF",()->broken.readAt(100,buffer,0,1));check(broken.bytes()==100);broken.close();
        Server server=new Server();RangeMediaSource source=reader(server,4L*1024*1024);check(source.getSize()==server.content.length);
        for(int pos:new int[]{0,8,12,4,4096,0}){check(source.readAt(pos,buffer,0,16)==16);for(int i=0;i<16;i++)check(buffer[i]==server.content[pos+i]);}
        check(server.requests==1);check(source.bytes()==512*1024);check(source.readAt(524280,buffer,0,16)==16);for(int i=0;i<16;i++)check(buffer[i]==server.content[524280+i]);source.readAt(0,buffer,0,16);check(server.requests==1);check(source.readAt(server.content.length,buffer,0,1)==-1);source.close();source.clearCache();fails("read after cancel",()->source.readAt(0,buffer,0,1));
        Server partial=new Server(1000);partial.part=100;RangeMediaSource pieces=reader(partial,10000);check(pieces.readAt(900,buffer,0,16)==16);check(partial.requests==10);check("\"v1\"".equals(partial.lastCondition));check(pieces.bytes()==1000);pieces.close();
        Server changed=new Server();changed.changeTag=true;RangeMediaSource stable=reader(changed,4000000);stable.readAt(0,buffer,0,1);fails("ETag replacement",()->stable.readAt(1048576,buffer,0,1));check("\"v1\"".equals(changed.lastCondition));fails("cached read after identity failure",()->stable.readAt(0,buffer,0,1));stable.close();
        for(int status:new int[]{200,416,500}){Server bad=new Server();bad.secondStatus=status;RangeMediaSource r=reader(bad,4000000);r.readAt(0,buffer,0,1);fails("invalid range HTTP "+status,()->r.readAt(1048576,buffer,0,1));r.close();}
        Server noTag=new Server();noTag.noTag=true;RangeMediaSource single=reader(noTag,4000000);check(single.readAt(0,buffer,0,1)==1);check(single.readAt(524288,buffer,0,1)==1);fails("unvalidated second response",()->single.readAt(1572864,buffer,0,1));single.close();
        for(int mode=0;mode<3;mode++){Server bad=new Server();bad.badRange=mode==0;bad.badLength=mode==1;bad.tls=mode==2;RangeMediaSource r=reader(bad,4000000);fails("headers/TLS mode "+mode,()->r.readAt(0,buffer,0,1));check(r.bytes()==0);r.close();}
        Server limited=new Server();RangeMediaSource capped=reader(limited,600000);capped.readAt(0,buffer,0,1);capped.readAt(524288,buffer,0,1);check(capped.bytes()==600000);fails("quota",()->capped.readAt(600000,buffer,0,1));check(capped.bytes()==600000);capped.close();
        System.out.println("Range streaming passed: truncation, partial ranges, stable identity, HTTP/TLS errors, single-response policy, cache, exact budget and cancellation.");
    }
}
