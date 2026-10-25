package tv.casa.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.LruCache;
import java.io.InputStream;

/** Shared immutable artwork. Icons are bounded; no Activity is retained. */
final class Assets {
    private static Bitmap landscape;
    private static final LruCache<String,Drawable.ConstantState> icons=new LruCache<>(24);
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
            Drawable source=context.getPackageManager().getApplicationIcon(pkg);
            int edge=Math.min(192,Math.max(96,Math.round(context.getResources().getDisplayMetrics().widthPixels/960f*64)));
            Bitmap thumbnail=Bitmap.createBitmap(edge,edge,Bitmap.Config.ARGB_8888);
            Rect bounds=new Rect(source.getBounds());source.setBounds(0,0,edge,edge);source.draw(new Canvas(thumbnail));source.setBounds(bounds);
            BitmapDrawable icon=new BitmapDrawable(context.getResources(),thumbnail);icon.setFilterBitmap(true);
            if(icon.getConstantState()!=null)icons.put(pkg,icon.getConstantState());
            return icon;
        }catch(Exception ignored){return null;}
    }
    static void clearIcons(){icons.evictAll();}
}
