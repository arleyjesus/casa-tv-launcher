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
    private static Bitmap home;private static String homePreset="";
    static void bindWallpaper(MainActivity owner,android.widget.ImageView view){
        String preset=owner.preset();if(home!=null&&preset.equals(homePreset)){view.setImageBitmap(home);return;}
        owner.loader.execute(()->{Bitmap result=null;try(InputStream in=owner.getAssets().open("home/"+preset+".webp")){BitmapFactory.Options o=new BitmapFactory.Options();o.inPreferredConfig=Bitmap.Config.ARGB_8888;result=BitmapFactory.decodeStream(in,null,o);}catch(Exception ignored){}final Bitmap bitmap=result;owner.handler.post(()->{if(owner.alive&&owner.root!=null&&view.getParent()==owner.root){if(bitmap!=null){home=bitmap;homePreset=preset;view.setImageBitmap(bitmap);}}});});
    }
    private static final LruCache<String,Drawable.ConstantState> icons=new LruCache<>(24);
    private static final LruCache<String,Drawable.ConstantState> banners=new LruCache<>(8);
    private static volatile int iconGeneration;
    private static final java.util.concurrent.ThreadPoolExecutor ICON_WORKER=new java.util.concurrent.ThreadPoolExecutor(1,1,30,java.util.concurrent.TimeUnit.SECONDS,new java.util.concurrent.ArrayBlockingQueue<Runnable>(32),r->{Thread t=new Thread(r,"VeliraIcons");t.setDaemon(true);return t;},new java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy());
    static void bindIcon(MainActivity owner,android.widget.ImageView view,String pkg){
        Object binding=new Object();view.setTag(binding);Drawable.ConstantState cached=icons.get(pkg);view.setImageDrawable(cached==null?null:cached.newDrawable(owner.getResources()));if(cached!=null)return;
        int generation=iconGeneration;ICON_WORKER.execute(()->{if(!owner.alive||generation!=iconGeneration)return;Drawable result=icon(owner.getApplicationContext(),pkg);owner.handler.post(()->{if(owner.alive&&generation==iconGeneration&&view.getTag()==binding)view.setImageDrawable(result);});});
    }
    static void bindHome(MainActivity owner,android.widget.ImageView view,String pkg,android.widget.TextView name){
        view.setTag(pkg);Drawable.ConstantState ready=banners.get(pkg);if(ready!=null){view.setImageDrawable(ready.newDrawable(owner.getResources()));name.setVisibility(android.view.View.GONE);return;}
        view.setImageDrawable(null);final int generation=iconGeneration;
        ICON_WORKER.execute(()->{if(!owner.alive||generation!=iconGeneration)return;Drawable result=banner(owner.getApplicationContext(),pkg);owner.handler.post(()->{if(owner.alive&&generation==iconGeneration&&pkg.equals(view.getTag())){view.setImageDrawable(result);boolean hasBanner=banners.get(pkg)!=null;name.setVisibility(hasBanner?android.view.View.GONE:android.view.View.VISIBLE);android.widget.FrameLayout.LayoutParams lp=(android.widget.FrameLayout.LayoutParams)view.getLayoutParams();lp.bottomMargin=owner.px(hasBanner?6:26);view.setLayoutParams(lp);}});});
    }
    private static Drawable banner(Context context,String pkg){
        int generation=iconGeneration;
        Drawable.ConstantState cached=banners.get(pkg);if(cached!=null)return cached.newDrawable(context.getResources());
        try{Drawable source=context.getPackageManager().getApplicationBanner(pkg);if(source==null)return icon(context,pkg);int w=320,h=180;Bitmap b=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);Rect old=new Rect(source.getBounds());source.setBounds(0,0,w,h);source.draw(new Canvas(b));source.setBounds(old);BitmapDrawable out=new BitmapDrawable(context.getResources(),b);out.setFilterBitmap(true);if(generation==iconGeneration&&out.getConstantState()!=null)banners.put(pkg,out.getConstantState());return out;}catch(Exception e){return icon(context,pkg);}
    }
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
        int generation=iconGeneration;
        Drawable.ConstantState state=icons.get(pkg);
        if(state!=null)return state.newDrawable(context.getResources());
        try{
            Drawable source=context.getPackageManager().getApplicationIcon(pkg);
            int edge=Math.min(192,Math.max(96,Math.round(context.getResources().getDisplayMetrics().widthPixels/960f*64)));
            Bitmap thumbnail=Bitmap.createBitmap(edge,edge,Bitmap.Config.ARGB_8888);
            Rect bounds=new Rect(source.getBounds());source.setBounds(0,0,edge,edge);source.draw(new Canvas(thumbnail));source.setBounds(bounds);
            BitmapDrawable icon=new BitmapDrawable(context.getResources(),thumbnail);icon.setFilterBitmap(true);
            if(generation==iconGeneration&&icon.getConstantState()!=null)icons.put(pkg,icon.getConstantState());
            return icon;
        }catch(Exception ignored){return null;}
    }
    static void clearIcons(){iconGeneration++;icons.evictAll();banners.evictAll();}
}
