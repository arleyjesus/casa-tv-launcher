package tv.casa.launcher;

/** Shared preference/backup contract. Bounds prevent accidental rapid slide loops. */
final class DisplayOptions {
    static final String[] STRING_KEYS={"preset","ambientEffect"};
    static final String[] BOOL_KEYS={"ambientMotion","ambientByTime","ambientRemote"};
    static final String[] INT_KEYS={"ambientIntervalSec","ambientTransitionMs","ambientZoomPercent"};
    static String defaultString(String key){return key.equals("preset")?"modern":"fade";}
    static int defaultInt(String key){return key.equals("ambientIntervalSec")?120:key.equals("ambientTransitionMs")?1000:2;}
    static boolean valid(String key,Object value){
        if(key.equals("preset"))return value instanceof String&&oneOf((String)value,"classic","modern","immersive");
        if(key.equals("ambientEffect"))return value instanceof String&&oneOf((String)value,"cut","fade");
        if(key.equals("ambientMotion")||key.equals("ambientByTime")||key.equals("ambientRemote"))return value instanceof Boolean;
        if(!(value instanceof Integer))return false;
        int n=(Integer)value;
        if(key.equals("ambientIntervalSec"))return contains(n,30,60,120,300,600);
        if(key.equals("ambientTransitionMs"))return contains(n,500,1000,2000);
        if(key.equals("ambientZoomPercent"))return contains(n,0,2,4);
        return false;
    }
    static boolean oneOf(String value,String... choices){for(String choice:choices)if(choice.equals(value))return true;return false;}
    static boolean contains(int value,int... choices){for(int choice:choices)if(choice==value)return true;return false;}
    static String presetLabel(String value){return value.equals("classic")?"Clásico":value.equals("immersive")?"Inmersivo":"Moderno";}
}
