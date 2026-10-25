package tv.casa.launcher;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.os.Handler;
import android.view.*;
import android.view.animation.LinearInterpolator;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/** Shared preview/screensaver. Only the current and incoming pictures are retained. */
final class AmbientScene extends FrameLayout {
    private final Handler handler=new Handler();
    private final FrameLayout pictures;
    private final ImageView[] images=new ImageView[2];
    private final ObjectAnimator[] motion=new ObjectAnimator[2];
    private final TextView clock;
    private ExecutorService decoder;
    private java.io.File[] remote=new java.io.File[0];
    private boolean running;private int generation,index,front,minute;
    private final SimpleDateFormat format=new SimpleDateFormat("HH:mm\nEEEE, d 'de' MMMM",new Locale("es"));
    private final Runnable advance=()->load((index+1)%(remote.length>0?remote.length:AmbientGallery.FILES.length),false);
    private final Runnable tick=new Runnable(){public void run(){if(!running)return;clock.setText(format.format(new Date()));clock.setTranslationX((minute%3-1)*12);clock.setTranslationY((minute%2)*8);minute++;handler.postDelayed(this,60000-(System.currentTimeMillis()%60000));}};

    AmbientScene(Context context){
        super(context);setBackgroundColor(0xFF101319);setClipChildren(true);
        pictures=new FrameLayout(context);pictures.setClipChildren(true);addView(pictures,new LayoutParams(-1,-1));
        for(int i=0;i<2;i++){images[i]=new ImageView(context);images[i].setScaleType(ImageView.ScaleType.CENTER_CROP);pictures.addView(images[i],new LayoutParams(-1,-1));}
        clock=new TextView(context);clock.setTextColor(MainActivity.INK);clock.setTextSize(27);clock.setTypeface(Typeface.create("serif",0));clock.setGravity(Gravity.RIGHT);clock.setLineSpacing(8,1.1f);clock.setShadowLayer(8,0,2,0xCC000000);
        LayoutParams lp=new LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.RIGHT);lp.setMargins(60,40,70,65);addView(clock,lp);
    }
    SharedPreferences prefs(){return Configuration.prefs(getContext());}
    int number(String key){int value=prefs().getInt(key,DisplayOptions.defaultInt(key));return DisplayOptions.valid(key,value)?value:DisplayOptions.defaultInt(key);}
    void start(){if(running)return;running=true;decoder=Executors.newSingleThreadExecutor();remote=prefs().getBoolean("ambientRemote",false)?new RemoteGallery(getContext()).local():new java.io.File[0];int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);index=prefs().getBoolean("ambientByTime",false)?(hour<8?1:hour<18?0:2):0;index=index%(remote.length>0?remote.length:AmbientGallery.FILES.length);load(index,true);tick.run();}
    void stop(){running=false;generation++;handler.removeCallbacksAndMessages(null);if(decoder!=null){decoder.shutdownNow();decoder=null;}for(int i=0;i<2;i++){images[i].animate().cancel();if(motion[i]!=null)motion[i].cancel();images[i].setImageDrawable(null);images[i].setScaleX(1);images[i].setScaleY(1);}}
    private void load(int next,boolean first){
        if(!running||decoder==null)return;final int request=++generation;final java.io.File[] selected=remote;
        decoder.execute(()->{android.graphics.Bitmap bitmap=selected.length>0?AmbientGallery.decodeFile(getContext(),selected[next],next):AmbientGallery.decode(getContext(),next);handler.post(()->{if(!running||request!=generation)return;display(bitmap,next,first,request);});});
    }
    private void display(Bitmap bitmap,int next,boolean first,int request){
        int incoming=first?0:1-front,old=1-incoming;ImageView view=images[incoming];view.animate().cancel();if(motion[incoming]!=null)motion[incoming].cancel();view.setScaleX(1);view.setScaleY(1);view.setImageBitmap(bitmap);view.bringToFront();
        boolean fade=!first&&prefs().getString("ambientEffect","fade").equals("fade");view.setAlpha(fade?0f:1f);
        if(fade)view.animate().alpha(1).setDuration(number("ambientTransitionMs")).withEndAction(()->{if(running&&generation==request){images[old].setImageDrawable(null);if(motion[old]!=null)motion[old].cancel();}}).start();
        else{images[old].setImageDrawable(null);if(motion[old]!=null)motion[old].cancel();}
        if(prefs().getBoolean("ambientMotion",false)&&number("ambientZoomPercent")>0){float end=1+number("ambientZoomPercent")/100f;motion[incoming]=ObjectAnimator.ofPropertyValuesHolder(view,PropertyValuesHolder.ofFloat("scaleX",1,end),PropertyValuesHolder.ofFloat("scaleY",1,end));motion[incoming].setInterpolator(new LinearInterpolator());motion[incoming].setDuration(number("ambientIntervalSec")*1000L);motion[incoming].start();}
        index=next;front=incoming;handler.removeCallbacks(advance);handler.postDelayed(advance,number("ambientIntervalSec")*1000L);
    }
}
