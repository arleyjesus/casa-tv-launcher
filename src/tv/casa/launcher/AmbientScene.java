package tv.casa.launcher;

import android.animation.*;
import android.content.*;
import android.graphics.*;
import android.os.Handler;
import android.view.*;
import android.view.animation.LinearInterpolator;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/** Preview and Dream share one lifecycle, two image slots and an optional streaming decoder. */
final class AmbientScene extends FrameLayout {
    private final Handler handler=new Handler();
    private final FrameLayout pictures;
    private final ImageView[] images=new ImageView[2];
    private final ObjectAnimator[] motion=new ObjectAnimator[2];
    private final TextView clock,credit,status;
    private final boolean forceVideo;
    private ExecutorService decoder;
    private OnlineAmbientStore store;
    private final ArrayList<Frame> frames=new ArrayList<>();
    private boolean running,videoReady;private int generation,index,front,minute,plannedNext,prefetchedIndex=-1;
    private Bitmap prefetched;
    private SurfaceView videoSurface;private SurfaceHolder.Callback surfaceEvents;private AmbientVideo video;
    private final Random random=new Random();
    private static final class Frame {java.io.File file;int fallback;String caption;}
    private final SimpleDateFormat format=new SimpleDateFormat("HH:mm\nEEEE, d 'de' MMMM",new Locale("es"));
    private final Runnable advance=()->showNext(plannedNext,false);
    private final Runnable tick=new Runnable(){public void run(){if(!running)return;clock.setText(format.format(new Date()));clock.setTranslationX((minute%3-1)*12);clock.setTranslationY((minute%2)*8);minute++;handler.postDelayed(this,60000-(System.currentTimeMillis()%60000));}};
    AmbientScene(Context c){this(c,false);}
    AmbientScene(Context c,boolean forceVideo){
        super(c);this.forceVideo=forceVideo;setBackgroundColor(0xFF000000);setClipChildren(true);
        pictures=new FrameLayout(c);pictures.setClipChildren(true);addView(pictures,new LayoutParams(-1,-1));
        for(int i=0;i<2;i++){images[i]=new ImageView(c);images[i].setScaleType(ImageView.ScaleType.CENTER_CROP);pictures.addView(images[i],new LayoutParams(-1,-1));}
        addView(new CinematicShade(c),new LayoutParams(-1,-1));
        clock=new TextView(c);clock.setTextColor(MainActivity.INK);clock.setTextSize(27);clock.setTypeface(Typeface.create("sans-serif-light",0));clock.setGravity(Gravity.RIGHT);clock.setLineSpacing(8,1.1f);clock.setShadowLayer(8,0,2,0xCC000000);
        LayoutParams cp=new LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.RIGHT);cp.setMargins(60,40,70,65);addView(clock,cp);
        credit=new TextView(c);credit.setTextSize(12);credit.setTextColor(AuraTheme.MUTED);credit.setMaxLines(2);credit.setShadowLayer(4,0,1,0xFF000000);LayoutParams lp=new LayoutParams(Math.round(c.getResources().getDisplayMetrics().widthPixels*.55f),-2,Gravity.BOTTOM|Gravity.LEFT);lp.setMargins(50,20,30,40);addView(credit,lp);
        status=new TextView(c);status.setTextSize(13);status.setTextColor(AuraTheme.INK);LayoutParams sp=new LayoutParams(-2,-2,Gravity.TOP|Gravity.LEFT);sp.setMargins(40,35,40,20);addView(status,sp);
    }
    SharedPreferences prefs(){return Configuration.prefs(getContext());}
    int number(String key){int value=prefs().getInt(key,DisplayOptions.defaultInt(key));return DisplayOptions.valid(key,value)?value:DisplayOptions.defaultInt(key);}
    void start(){if(running)return;running=true;videoReady=false;final int session=++generation;decoder=Executors.newSingleThreadExecutor();store=new OnlineAmbientStore(getContext());applyFilters();pictures.setVisibility(VISIBLE);
        decoder.execute(()->{ArrayList<Frame> list=new ArrayList<>();try{if(store.prefs().getBoolean("enabled",false)){AmbientCatalog data=store.catalog();for(AmbientCatalog.Item item:store.local(data)){Frame f=new Frame();f.file=store.file(item.choose(store.targetWidth()));f.caption=item.title+" · "+item.author+" · "+item.license;list.add(f);}}}catch(Exception ignored){}
            if(list.isEmpty()&&prefs().getBoolean("ambientRemote",false)){java.io.File[] old=new RemoteGallery(getContext()).local();for(int i=0;i<old.length;i++){Frame f=new Frame();f.file=old[i];f.fallback=i;f.caption="Paisajes Casa Aura · MIT";list.add(f);}}
            if(list.isEmpty())for(int i=0;i<AmbientGallery.FILES.length;i++){Frame f=new Frame();f.fallback=i;f.caption="";list.add(f);}
            handler.post(()->{if(!running||session!=generation)return;frames.clear();frames.addAll(list);int hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);index=prefs().getBoolean("ambientByTime",false)?(hour<8?1:hour<18?0:2)%frames.size():0;if(store.prefs().getBoolean("random",false))index=random.nextInt(frames.size());showNext(index,true);});
        });tick.run();if(forceVideo||store.prefs().getBoolean("videoAuto",false))openVideo();
    }
    private void applyFilters(){float saturation=number("ambientSaturation")/100f,contrast=number("ambientContrast")/100f,offset=127.5f*(1-contrast)+number("ambientBrightness")*2.55f;ColorMatrix color=new ColorMatrix();color.setSaturation(saturation);color.postConcat(new ColorMatrix(new float[]{contrast,0,0,0,offset,0,contrast,0,0,offset,0,0,contrast,0,offset,0,0,0,1,0}));for(ImageView view:images){if(saturation==1f&&contrast==1f&&number("ambientBrightness")==0)view.clearColorFilter();else view.setColorFilter(new ColorMatrixColorFilter(color));}}
    private Bitmap decode(Frame f){return f.file==null?AmbientGallery.decode(getContext(),f.fallback):AmbientGallery.decodeFile(getContext(),f.file,f.fallback);}
    private void showNext(int next,boolean first){if(!running||videoReady||decoder==null||frames.isEmpty())return;final int session=generation;if(prefetched!=null&&prefetchedIndex==next){Bitmap ready=prefetched;prefetched=null;prefetchedIndex=-1;display(ready,next,first,session);return;}final Frame selected=frames.get(next);decoder.execute(()->{Bitmap bitmap=decode(selected);handler.post(()->{if(running&&!videoReady&&session==generation)display(bitmap,next,first,session);});});}
    private void display(Bitmap bitmap,int next,boolean first,int session){
        if(!running||videoReady||session!=generation)return;int incoming=first?0:1-front,old=1-incoming;ImageView view=images[incoming];view.animate().cancel();if(motion[incoming]!=null)motion[incoming].cancel();view.setScaleX(1);view.setScaleY(1);view.setTranslationX(0);view.setImageBitmap(bitmap);view.bringToFront();
        String effect=prefs().getString("ambientEffect","fade");boolean transition=!first&&!effect.equals("cut");view.setAlpha(transition?0f:1f);index=next;front=incoming;credit.setText(frames.get(next).caption);
        Runnable settled=()->{if(!running||videoReady||session!=generation)return;images[old].setImageDrawable(null);if(motion[old]!=null)motion[old].cancel();startMotion(incoming);preload(session);};
        if(transition){if(effect.equals("slide"))view.setTranslationX(getWidth()*.08f);if(effect.equals("zoom")){view.setScaleX(1.03f);view.setScaleY(1.03f);}view.animate().alpha(1).translationX(0).scaleX(1).scaleY(1).setDuration(number("ambientTransitionMs")).withEndAction(settled).start();}else settled.run();
        handler.removeCallbacks(advance);handler.postDelayed(advance,number("ambientIntervalSec")*1000L);
    }
    private void startMotion(int slot){if(!prefs().getBoolean("ambientMotion",false)||number("ambientZoomPercent")==0)return;float end=1+number("ambientZoomPercent")/100f;motion[slot]=ObjectAnimator.ofPropertyValuesHolder(images[slot],PropertyValuesHolder.ofFloat("scaleX",1,end),PropertyValuesHolder.ofFloat("scaleY",1,end));motion[slot].setInterpolator(new LinearInterpolator());motion[slot].setDuration(number("ambientIntervalSec")*1000L);motion[slot].start();}
    private void preload(int session){plannedNext=frames.size()>1&&store.prefs().getBoolean("random",false)?(index+1+random.nextInt(frames.size()-1))%frames.size():(index+1)%frames.size();final int target=plannedNext;final Frame selected=frames.get(target);decoder.execute(()->{Bitmap next=decode(selected);handler.post(()->{if(running&&!videoReady&&session==generation){prefetched=next;prefetchedIndex=target;}});});}
    private void openVideo(){
        String url=store.prefs().getString("videoUrl","");if(!OnlinePolicy.https(url)){status.setText("Elige un vídeo en Ambiente online");return;}
        videoSurface=new SurfaceView(getContext());videoSurface.setZOrderOnTop(false);addView(videoSurface,0,new LayoutParams(-1,-1));surfaceEvents=new SurfaceHolder.Callback(){
            public void surfaceCreated(SurfaceHolder holder){if(!running)return;int width=store.prefs().getInt("videoWidth",1920);if(width!=1280&&width!=1920)width=1920;int minutes=store.prefs().getInt("videoMinutes",15);if(minutes!=5&&minutes!=15&&minutes!=30)minutes=15;
                video=new AmbientVideo(getContext(),new AmbientVideo.Events(){public void ready(){if(!running)return;videoReady=true;handler.removeCallbacks(advance);prefetched=null;for(int i=0;i<2;i++){images[i].animate().cancel();if(motion[i]!=null)motion[i].cancel();images[i].setImageDrawable(null);}pictures.setVisibility(GONE);credit.setText(store.prefs().getString("videoCredit","Vídeo personalizado"));status.setText("");}public void failed(String reason){if(!running)return;videoReady=false;videoSurface.setVisibility(GONE);pictures.setVisibility(VISIBLE);status.setText(reason);if(!frames.isEmpty())showNext(index,true);}public void size(int vw,int vh){}},store.prefs().getBoolean("wifiOnly",true),width,minutes);
                status.setText("Preparando vídeo online…");video.start(url,holder.getSurface(),OnlinePolicy.videoBudget(store.prefs().getInt("videoBudgetMiB",100)));
            }
            public void surfaceChanged(SurfaceHolder holder,int format,int w,int h){}
            public void surfaceDestroyed(SurfaceHolder holder){if(video!=null)video.stop();}
        };videoSurface.getHolder().addCallback(surfaceEvents);
    }

    void stop(){if(!running)return;running=false;generation++;handler.removeCallbacksAndMessages(null);if(video!=null){video.stop();video=null;}if(videoSurface!=null){videoSurface.getHolder().removeCallback(surfaceEvents);removeView(videoSurface);videoSurface=null;}if(decoder!=null){decoder.shutdownNow();decoder=null;}prefetched=null;prefetchedIndex=-1;for(int i=0;i<2;i++){images[i].animate().cancel();if(motion[i]!=null)motion[i].cancel();images[i].setImageDrawable(null);images[i].setScaleX(1);images[i].setScaleY(1);}frames.clear();credit.setText("");status.setText("");}
}
