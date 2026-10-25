package tv.casa.launcher;

/** Narrow contract for the one TCL control validated on C645/V652. */
final class PictureTogglePolicy {
    static final String KEY="adaptive_picture_content_0", CREATOR="com.tcl.settings";
    static void validate(String key,int enabled,int actionCount,String creator){
        if(!KEY.equals(key)||enabled!=1||actionCount!=1||!CREATOR.equals(creator))throw new IllegalStateException("Unsupported picture control");
    }
    static boolean shouldSend(Boolean current,boolean desired){
        if(current==null)throw new IllegalStateException("Picture state unavailable");
        return current.booleanValue()!=desired;
    }
}
