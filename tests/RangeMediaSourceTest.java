package tv.casa.launcher;
import java.io.*;
import java.net.*;
public final class RangeMediaSourceTest {
    static void check(boolean value){if(!value)throw new AssertionError();}
    static final class Server extends AmbientHttp {
        final byte[] content=new byte[2*1024*1024];int requests;
        Server(){for(int i=0;i<content.length;i++)content[i]=(byte)(i%251);}
        HttpURLConnection open(String address,String range,String tag)throws Exception{requests++;final int start=Integer.parseInt(range.substring(6,range.length()-1));return new HttpURLConnection(new URL(address)){
            public int getResponseCode(){return 206;}public String getHeaderField(String name){return name.equals("Content-Range")?"bytes "+start+"-"+(content.length-1)+"/"+content.length:null;}
            public InputStream getInputStream(){return new ByteArrayInputStream(content,start,content.length-start);}public void disconnect(){}public boolean usingProxy(){return false;}public void connect(){}
        };}
        void cancel(){cancelled=true;}
    }
    public static void main(String[] args)throws Exception{
        Server server=new Server();RangeMediaSource reader=new RangeMediaSource("https://test.invalid/v.mp4",4L*1024*1024,server);byte[] buffer=new byte[16];check(reader.getSize()==server.content.length);
        for(int pos:new int[]{0,8,12,4,4096,0}){check(reader.readAt(pos,buffer,0,16)==16);for(int i=0;i<16;i++)check(buffer[i]==server.content[pos+i]);}
        check(server.requests==1);check(reader.bytes()==512*1024);check(reader.readAt(524280,buffer,0,16)==16);for(int i=0;i<16;i++)check(buffer[i]==server.content[524280+i]);reader.readAt(0,buffer,0,16);check(server.requests==1);reader.close();reader.clearCache();try{reader.readAt(0,buffer,0,1);throw new AssertionError();}catch(IOException expected){}
        Server limited=new Server();RangeMediaSource capped=new RangeMediaSource("https://test.invalid/v.mp4",600000,limited);capped.readAt(0,buffer,0,1);capped.readAt(524288,buffer,0,1);check(capped.bytes()==600000);try{capped.readAt(600000,buffer,0,1);throw new AssertionError();}catch(IOException expected){}check(capped.bytes()==600000);capped.close();
        System.out.println("Range streaming passed: repeated MP4 headers reuse one request, backward reads use cache, exact payload budget and cancellation.");
    }
}
