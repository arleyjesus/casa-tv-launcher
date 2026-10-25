package tv.casa.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;

final class AmbientGallery {
    static final String[] FILES={"landscape.png","ambient-fjord.png","ambient-forest.png"};
    static final String[] NAMES={"Desierto","Fiordo","Bosque"};
    static Bitmap decode(Context context,int index){
        if(index==0)return Assets.landscape(context);
        try{
            BitmapFactory.Options options=new BitmapFactory.Options();options.inJustDecodeBounds=true;
            try(InputStream input=context.getAssets().open(FILES[index])){BitmapFactory.decodeStream(input,null,options);}
            int target=Math.min(1920,context.getResources().getDisplayMetrics().widthPixels),sample=1;
            while(options.outWidth/(sample*2)>=target)sample*=2;
            options.inJustDecodeBounds=false;options.inSampleSize=sample;options.inPreferredConfig=Bitmap.Config.RGB_565;options.inScaled=false;
            try(InputStream input=context.getAssets().open(FILES[index])){return BitmapFactory.decodeStream(input,null,options);}
        }catch(Exception error){android.util.Log.w("Casa","Ambient image unavailable",error);return Assets.landscape(context);}
    }
}
