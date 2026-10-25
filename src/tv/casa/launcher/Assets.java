package tv.casa.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import java.io.InputStream;

/** Shared immutable artwork. Icons are bounded; no Activity is retained. */
final class Assets {
    private static Bitmap landscape;
    private static final LruCache<String,Drawable.ConstantState> icons=new LruCache<>(12);
    static synchronized Bitmap landscape(Context context){
        if(landscape==null){
            try(InputStream stream=context.getAssets().open("landscape.png")){
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inPreferredConfig=Bitmap.Config.RGB_565;
                landscape=BitmapFactory.decodeStream(stream,null,options);
            }catch(Exception error){android.util.Log.w("Casa","Artwork unavailable",error);}
        }
        return landscape;
    }
    static Drawable icon(Context context,String pkg){
        Drawable.ConstantState state=icons.get(pkg);
        if(state!=null)return state.newDrawable(context.getResources());
        try{
            Drawable icon=context.getPackageManager().getApplicationIcon(pkg);
            if(icon.getConstantState()!=null)icons.put(pkg,icon.getConstantState());
            return icon;
        }catch(Exception ignored){return null;}
    }
    static void clearIcons(){icons.evictAll();}
}
