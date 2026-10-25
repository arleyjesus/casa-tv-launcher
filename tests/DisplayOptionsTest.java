package tv.casa.launcher;
public final class DisplayOptionsTest {
    static void check(boolean value){if(!value)throw new AssertionError();}
    public static void main(String[] args){
        for(String p:new String[]{"classic","modern","immersive"})check(DisplayOptions.valid("preset",p));
        check(!DisplayOptions.valid("preset","unknown"));check(!DisplayOptions.valid("preset",null));
        check(!DisplayOptions.valid("ambientIntervalSec",0));check(!DisplayOptions.valid("ambientIntervalSec",1));check(!DisplayOptions.valid("ambientIntervalSec",30.0));
        check(DisplayOptions.valid("ambientIntervalSec",30));check(DisplayOptions.valid("ambientIntervalSec",600));
        check(!DisplayOptions.valid("ambientTransitionMs",60000));check(DisplayOptions.valid("ambientTransitionMs",2000));
        check(!DisplayOptions.valid("ambientZoomPercent",50));check(DisplayOptions.valid("ambientZoomPercent",4));
        check(DisplayOptions.valid("ambientMotion",false));check(!DisplayOptions.valid("ambientMotion","false"));
        for(String key:DisplayOptions.STRING_KEYS)check(DisplayOptions.valid(key,DisplayOptions.defaultString(key)));
        for(String key:DisplayOptions.INT_KEYS)check(DisplayOptions.valid(key,DisplayOptions.defaultInt(key)));
        System.out.println("Display option tests passed: presets, defaults, interval bounds, transition and zoom limits, strict value types.");
    }
}
