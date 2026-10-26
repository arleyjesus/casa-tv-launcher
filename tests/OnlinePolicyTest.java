package tv.casa.launcher;
public final class OnlinePolicyTest {
    static void check(boolean ok){if(!ok)throw new AssertionError();}
    public static void main(String[] args)throws Exception{
        check(OnlinePolicy.jsonDepth("{\"text\":\"[not a structure]\"}"));check(!OnlinePolicy.jsonDepth(new String(new char[25]).replace('\0','[')+new String(new char[25]).replace('\0',']')));
        check(OnlinePolicy.https("https://example.org/a.jpg?version=2"));
        for(String url:new String[]{"http://example.org/a","file:///sdcard/a","javascript:1","https://user:pass@example.org/a","https://example.org:8080/a","https://example.org/a#x",null})check(!OnlinePolicy.https(url));
        check(OnlinePolicy.dimensions(3840,2160));check(OnlinePolicy.dimensions(4032,3024));check(!OnlinePolicy.dimensions(5000,2000));check(!OnlinePolicy.dimensions(-1,1080));
        check(OnlinePolicy.imageSample(3840,2160,1920,1080)==2);check(OnlinePolicy.imageSample(3840,2160,3840,2160)==1);check(OnlinePolicy.imageSample(4096,4096,3840,2160)==2);
        check(OnlinePolicy.videoBudget(-1)==100L*1024*1024);check(OnlinePolicy.videoBudget(25)==25L*1024*1024);
        check(RangePolicy.length("bytes 100-199/1000",100)==1000);
        for(String value:new String[]{"bytes 0-99/1000","bytes 100-90/1000","bytes 100-999/999","bytes 100-199/*","bytes 100-199/999999999999999999999999",null})try{RangePolicy.length(value,100);throw new AssertionError();}catch(java.io.IOException expected){}
        System.out.println("Online policy passed: URLs, source and decoded dimensions, session budgets, valid and malformed HTTP ranges.");
    }
}
