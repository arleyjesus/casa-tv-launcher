package tv.casa.launcher;

import java.io.*;
import java.net.*;

/** Bounded HTTPS redirects, normal certificate verification and cancellable sockets. */
class AmbientHttp {
    volatile HttpURLConnection active;volatile boolean cancelled;String lastEtag="";
    HttpURLConnection open(String address,String range,String etag)throws Exception{
        for(int redirect=0;redirect<=3;redirect++){
            check();if(!OnlinePolicy.https(address))throw new IOException("Se requiere HTTPS");
            HttpURLConnection c=(HttpURLConnection)new URL(address).openConnection();active=c;
            c.setConnectTimeout(10000);c.setReadTimeout(10000);c.setInstanceFollowRedirects(false);c.setUseCaches(false);c.setRequestProperty("Accept-Encoding","identity");c.setRequestProperty("User-Agent","CasaAuraTV/0.10");
            if(range!=null)c.setRequestProperty("Range",range);if(etag!=null&&!etag.isEmpty())c.setRequestProperty("If-None-Match",etag);
            int status=c.getResponseCode();if(status==301||status==302||status==303||status==307||status==308){String next=c.getHeaderField("Location");c.disconnect();if(next==null)throw new IOException("Redirección no válida");address=new URL(new URL(address),next).toString();continue;}return c;
        }throw new IOException("Demasiadas redirecciones");
    }
    byte[] fetch(String url,int limit,String etag)throws Exception{HttpURLConnection c=open(url,null,etag);try{lastEtag=c.getHeaderField("ETag");if(c.getResponseCode()==304)return null;if(c.getResponseCode()!=200||c.getContentLengthLong()>limit)throw new IOException("No se pudo descargar el recurso");ByteArrayOutputStream out=new ByteArrayOutputStream();try(InputStream in=c.getInputStream()){byte[] b=new byte[16384];int n;while((n=in.read(b))!=-1){check();if(out.size()+n>limit)throw new IOException("Recurso demasiado grande");out.write(b,0,n);}}return out.toByteArray();}finally{c.disconnect();}}
    void check()throws InterruptedIOException{if(cancelled||Thread.currentThread().isInterrupted())throw new InterruptedIOException("Cancelado");}
    void cancel(){cancelled=true;HttpURLConnection c=active;if(c!=null)c.disconnect();}
}
