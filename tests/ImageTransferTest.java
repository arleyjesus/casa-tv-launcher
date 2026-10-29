package tv.casa.launcher;
import java.io.*;
public final class ImageTransferTest {
    static void check(boolean b){if(!b)throw new AssertionError();}
    interface Action{void run()throws Exception;}
    static void fails(Action f)throws Exception{try{f.run();throw new AssertionError();}catch(IOException expected){}}
    public static void main(String[] args)throws Exception{
        ByteArrayOutputStream out=new ByteArrayOutputStream();String hash=ImageTransfer.copy(new ByteArrayInputStream(new byte[]{97,98,99}),out,3,()->{});
        check(hash.equals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"));check(out.size()==3);
        fails(()->ImageTransfer.copy(new ByteArrayInputStream(new byte[2]),new ByteArrayOutputStream(),3,()->{}));
        ByteArrayOutputStream bounded=new ByteArrayOutputStream();fails(()->ImageTransfer.copy(new ByteArrayInputStream(new byte[5]),bounded,3,()->{}));check(bounded.size()<=3);
        int[] checks={0};ByteArrayOutputStream cancelled=new ByteArrayOutputStream();fails(()->ImageTransfer.copy(new ByteArrayInputStream(new byte[32768]),cancelled,32768,()->{if(++checks[0]>=3)throw new InterruptedIOException();}));check(cancelled.size()==16384);
        System.out.println("Image transfer passed: incremental SHA, exact size, truncation, overflow and cancellation.");
    }
}
