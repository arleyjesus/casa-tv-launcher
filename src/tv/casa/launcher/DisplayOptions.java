package tv.casa.launcher;

/** Shared preference/backup contract. Bounds prevent accidental rapid slide loops. */
final class DisplayOptions {
    static final String[] STRING_KEYS={"preset","ambientEffect","visualEffects"};
    static final String[] BOOL_KEYS={"ambientMotion","ambientByTime","ambientRemote"};
    static final String[] INT_KEYS={"ambientIntervalSec","ambientTransitionMs","ambientZoomPercent","ambientSaturation","ambientContrast","ambientBrightness"};
    static String defaultString(String key){return key.equals("preset")?"modern":key.equals("visualEffects")?"low":"fade";}
    static int defaultInt(String key){return key.equals("ambientIntervalSec")?120:key.equals("ambientTransitionMs")?1000:key.equals("ambientSaturation")||key.equals("ambientContrast")?100:key.equals("ambientBrightness")?0:2;}
    static boolean valid(String key,Object value){
        if(key.equals("preset"))return value instanceof String&&oneOf((String)value,"classic","modern","immersive");
        if(key.equals("visualEffects"))return value instanceof String&&oneOf((String)value,"off","low","medium","high");
        if(key.equals("ambientEffect"))return value instanceof String&&oneOf((String)value,"cut","fade","slide","zoom");
        if(key.equals("ambientMotion")||key.equals("ambientByTime")||key.equals("ambientRemote"))return value instanceof Boolean;
        if(!(value instanceof Integer))return false;
        int n=(Integer)value;
        if(key.equals("ambientIntervalSec"))return contains(n,30,60,120,300,600);
        if(key.equals("ambientTransitionMs"))return contains(n,500,1000,2000);
        if(key.equals("ambientSaturation")||key.equals("ambientContrast"))return contains(n,75,100,125);
        if(key.equals("ambientBrightness"))return contains(n,-20,-10,0,10,20);
        if(key.equals("ambientZoomPercent"))return contains(n,0,2,4);
        return false;
    }
    static boolean oneOf(String value,String... choices){for(String choice:choices)if(choice.equals(value))return true;return false;}
    static boolean contains(int value,int... choices){for(int choice:choices)if(choice==value)return true;return false;}
    static String effectLabel(String value){return "cut".equals(value)?"Corte directo":"slide".equals(value)?"Desplazamiento":"zoom".equals(value)?"Zoom suave":"Disolución";}
    static String presetLabel(String value){return value.equals("classic")?"Clásico":value.equals("immersive")?"Inmersivo":"Moderno";}
}
