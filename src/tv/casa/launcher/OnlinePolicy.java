package tv.casa.launcher;

import java.net.URI;

final class OnlinePolicy {
    static final int MAX_CATALOG=262144,MAX_ITEMS=48,MAX_COLLECTIONS=12,MAX_IMAGE=8*1024*1024;
    static final long CACHE_BYTES=64L*1024*1024,MAX_PIXELS=4096L*4096;
    static final String DEFAULT_CATALOG="https://raw.githubusercontent.com/arleyjesus/casa-tv-launcher/main/resources/ambient/catalog-v2.json";
    static boolean https(String value){if(value==null||value.length()>2048)return false;try{URI u=new URI(value);return "https".equalsIgnoreCase(u.getScheme())&&u.getHost()!=null&&u.getRawUserInfo()==null&&u.getRawFragment()==null&&(u.getPort()==-1||u.getPort()==443);}catch(Exception e){return false;}}
    static boolean jsonDepth(String text){if(text==null||text.length()>MAX_CATALOG)return false;int depth=0;boolean string=false,escape=false;for(int i=0;i<text.length();i++){char c=text.charAt(i);if(string){if(escape)escape=false;else if(c=='\\')escape=true;else if(c=='"')string=false;}else if(c=='"')string=true;else if(c=='{'||c=='['){if(++depth>24)return false;}else if(c=='}'||c==']'){if(--depth<0)return false;}}return depth==0&&!string;}
    static boolean id(String s){return s!=null&&s.matches("[a-z0-9][a-z0-9-]{0,63}");}
    static boolean hash(String s){return s!=null&&s.matches("[a-f0-9]{64}");}
    static boolean dimensions(int w,int h){return w>0&&h>0&&w<=4096&&h<=4096&&(long)w*h<=MAX_PIXELS;}
    static String text(String s,int max){if(s==null)return "";s=s.replaceAll("[\\p{Cntrl}]"," ").trim();return s.substring(0,Math.min(max,s.length()));}
    static long videoBudget(int mib){return (mib==25||mib==100||mib==250?mib:100)*1024L*1024;}
    static int imageSample(int w,int h,int screenWidth,int screenHeight){int sample=1;int tw=Math.min(3840,Math.max(1,screenWidth)),th=Math.min(2160,Math.max(1,screenHeight));while(w/(sample*2)>=tw&&h/(sample*2)>=th)sample*=2;while((long)(w/sample)*(h/sample)>4096L*2160)sample*=2;return sample;}
}
