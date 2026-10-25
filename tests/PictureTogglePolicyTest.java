package tv.casa.launcher;
public final class PictureTogglePolicyTest {
    static void check(boolean v){if(!v)throw new AssertionError();}
    static void rejects(String key,int enabled,int count,String creator){try{PictureTogglePolicy.validate(key,enabled,count,creator);throw new AssertionError();}catch(IllegalStateException expected){}}
    public static void main(String[] args){
        PictureTogglePolicy.validate("adaptive_picture_content_0",1,1,"com.tcl.settings");
        rejects("brightness",1,1,"com.tcl.settings");rejects(PictureTogglePolicy.KEY,0,1,"com.tcl.settings");rejects(PictureTogglePolicy.KEY,1,2,"com.tcl.settings");rejects(PictureTogglePolicy.KEY,1,0,null);rejects(PictureTogglePolicy.KEY,1,1,"other.app");
        check(!PictureTogglePolicy.shouldSend(true,true));check(!PictureTogglePolicy.shouldSend(false,false));check(PictureTogglePolicy.shouldSend(false,true));check(PictureTogglePolicy.shouldSend(true,false));
        try{PictureTogglePolicy.shouldSend(null,true);throw new AssertionError();}catch(IllegalStateException expected){}
        System.out.println("Picture policy passed: exact TCL row, enabled control, single trusted action, unavailable and already-matching state.");
    }
}
