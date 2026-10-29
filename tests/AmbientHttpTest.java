package tv.casa.launcher;
import java.io.*;import java.net.*;import java.util.*;
public final class AmbientHttpTest {
    static final Map<String,String> seen=new HashMap<>();static int requests;
    static void check(boolean value){if(!value)throw new AssertionError();}
    interface Job {void run()throws Exception;}
    static void fails(Job task)throws Exception{try{task.run();throw new AssertionError();}catch(IOException expected){}}
    public static void main(String[] args)throws Exception{
        URL.setURLStreamHandlerFactory(protocol->protocol.equals("https")?new URLStreamHandler(){protected URLConnection openConnection(URL url){requests++;return new HttpURLConnection(url){
            final String path=url.getPath();
            public int getResponseCode()throws IOException{seen.put("range",getRequestProperty("Range"));seen.put("condition",getRequestProperty("If-Range"));seen.put("etag",getRequestProperty("If-None-Match"));if(path.equals("/tls"))throw new javax.net.ssl.SSLHandshakeException("test TLS error");return path.startsWith("/r")||path.equals("/downgrade")||path.equals("/loop")?302:path.equals("/cached")?304:200;}
            public String getHeaderField(String key){if(!key.equals("Location"))return key.equals("ETag")?"\"one\"":null;if(path.equals("/downgrade"))return "http://test.invalid/file";if(path.equals("/loop"))return "/loop";int n=Integer.parseInt(path.substring(2));return n>=3?"/file":"/r"+(n+1);}
            public long getContentLengthLong(){return 3;}
            public InputStream getInputStream(){return new ByteArrayInputStream(new byte[]{1,2,3});}public void disconnect(){}public boolean usingProxy(){return false;}public void connect(){}
        };}}:null);
        AmbientHttp h=new AmbientHttp();requests=0;check(h.fetch("https://test.invalid/r1",3,null).length==3);check(requests==4);
        fails(()->new AmbientHttp().open("https://test.invalid/loop",null,null));
        fails(()->new AmbientHttp().open("https://test.invalid/downgrade",null,null));
        fails(()->new AmbientHttp().open("https://test.invalid/tls",null,null));
        fails(()->new AmbientHttp().fetch("https://test.invalid/file",2,null));
        AmbientHttp range=new AmbientHttp();range.open("https://test.invalid/file","bytes=10-","\"v1\"");check("bytes=10-".equals(seen.get("range")));check("\"v1\"".equals(seen.get("condition")));check(seen.get("etag")==null);
        AmbientHttp cached=new AmbientHttp();check(cached.fetch("https://test.invalid/cached",10,"\"one\"")==null);check("\"one\"".equals(seen.get("etag")));check(seen.get("condition")==null);
        AmbientHttp cancelled=new AmbientHttp();cancelled.cancel();fails(()->cancelled.open("https://test.invalid/file",null,null));
        System.out.println("HTTPS policy passed: redirects, downgrade rejection, TLS propagation, conditional headers, 304, size and cancellation.");
    }
}
