package tv.casa.launcher;
import java.io.*;
import java.security.*;
/** Bounded streaming transfer: no full image byte array or second hash pass. */
final class ImageTransfer {
    interface Check {void run()throws IOException;}
    static String copy(InputStream in,OutputStream out,int expected,Check check)throws Exception{
        if(expected<1||expected>OnlinePolicy.MAX_IMAGE)throw new IOException("Tamaño de imagen no válido");
        MessageDigest digest=MessageDigest.getInstance("SHA-256");byte[] block=new byte[16384];int count=0;
        for(;;){check.run();int n=in.read(block);if(n<0)break;if(n==0)throw new IOException("Descarga sin progreso");if(n>expected-count)throw new IOException("Imagen demasiado grande");check.run();out.write(block,0,n);digest.update(block,0,n);count+=n;}
        if(count!=expected)throw new EOFException("Imagen truncada");check.run();StringBuilder hex=new StringBuilder(64);for(byte b:digest.digest())hex.append(String.format(java.util.Locale.ROOT,"%02x",b&255));return hex.toString();
    }
}
