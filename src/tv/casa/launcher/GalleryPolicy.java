package tv.casa.launcher;

import java.io.*;
import java.net.URI;
import java.security.MessageDigest;

/** Limits shared by the downloader and its host-side tests. */
final class GalleryPolicy {
    static final int MAX_IMAGES=3, MAX_CATALOG=65536, MAX_IMAGE=4*1024*1024;
    static final long MAX_PIXELS=4096L*2160;
    static final String BASE="https://raw.githubusercontent.com/arleyjesus/velira-tv/";
    static final String CATALOG=BASE+"main/resources/ambient-catalog-v1.json";
    static boolean imageUrl(String url){
        try{URI uri=new URI(url);return "https".equals(uri.getScheme())&&"raw.githubusercontent.com".equals(uri.getHost())&&uri.getPort()==-1&&uri.getRawQuery()==null&&uri.getRawFragment()==null&&uri.getRawUserInfo()==null&&uri.getRawPath().matches("/arleyjesus/(?:velira-tv|casa-tv-launcher)/v[0-9]+\\.[0-9]+\\.[0-9]+/assets/[a-z0-9-]+\\.png");}catch(Exception e){return false;}
    }
    static boolean filename(String value){return value!=null&&value.matches("[a-z0-9-]{1,64}\\.png");}
    static void checkInterrupted()throws InterruptedIOException{if(Thread.currentThread().isInterrupted())throw new InterruptedIOException("Descarga cancelada");}
    static long copy(InputStream input,OutputStream output,long limit)throws IOException{
        byte[] block=new byte[16384];long total=0;int n;
        while(true){checkInterrupted();n=input.read(block);if(n==-1)break;total+=n;if(total>limit)throw new IOException("Archivo demasiado grande");output.write(block,0,n);}return total;
    }
    static String sha256(File file)throws Exception{
        MessageDigest hash=MessageDigest.getInstance("SHA-256");try(InputStream input=new FileInputStream(file)){byte[] block=new byte[16384];int n;while((n=input.read(block))!=-1){checkInterrupted();hash.update(block,0,n);}}
        StringBuilder result=new StringBuilder();for(byte b:hash.digest())result.append(String.format(java.util.Locale.ROOT,"%02x",b&255));return result.toString();
    }
}
