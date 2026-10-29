package tv.casa.launcher;
import java.io.*;
public final class GalleryPolicyTest {
    static void check(boolean condition){if(!condition)throw new AssertionError();}
    public static void main(String[] args)throws Exception{
        check(GalleryPolicy.imageUrl(GalleryPolicy.BASE+"v0.6.0/assets/ambient-fjord.png"));
        for(String url:new String[]{"http://raw.githubusercontent.com/arleyjesus/velira-tv/v0.6.0/assets/a.png",GalleryPolicy.BASE+"main/assets/a.png",GalleryPolicy.BASE+"v0.6.0/assets/../a.png",GalleryPolicy.BASE+"v0.6.0/assets/%2e%2e/a.png",GalleryPolicy.BASE+"v0.6.0/assets/a.png?token=x","https://raw.githubusercontent.com.evil.test/arleyjesus/casa-tv-launcher/v0.6.0/assets/a.png","https://user@raw.githubusercontent.com/arleyjesus/velira-tv/v0.6.0/assets/a.png",GalleryPolicy.BASE+"v0.6.0/assets/a.png#x"})check(!GalleryPolicy.imageUrl(url));
        check(GalleryPolicy.filename("ambient-fjord.png"));for(String name:new String[]{"../a.png","a/b.png",".png","a.jpg","A.png"})check(!GalleryPolicy.filename(name));
        ByteArrayOutputStream out=new ByteArrayOutputStream();check(GalleryPolicy.copy(new ByteArrayInputStream(new byte[4]),out,4)==4);
        try{GalleryPolicy.copy(new ByteArrayInputStream(new byte[5]),new ByteArrayOutputStream(),4);throw new AssertionError();}catch(IOException expected){}
        Thread.currentThread().interrupt();try{GalleryPolicy.copy(new ByteArrayInputStream(new byte[1]),new ByteArrayOutputStream(),4);throw new AssertionError();}catch(InterruptedIOException expected){}finally{Thread.interrupted();}
        File file=File.createTempFile("casa-hash-",".tmp");try{try(OutputStream output=new FileOutputStream(file)){output.write(new byte[]{97,98,99});}check(GalleryPolicy.sha256(file).equals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"));}finally{file.delete();}
        System.out.println("Gallery tests passed: trusted URLs, traversal rejection, size bounds, cancellation and SHA-256.");
    }
}
