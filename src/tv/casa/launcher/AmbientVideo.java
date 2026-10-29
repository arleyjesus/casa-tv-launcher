package tv.casa.launcher;

import android.content.Context;
import android.net.*;
import android.media.MediaPlayer;
import android.os.*;
import android.view.Surface;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/** One process-wide serial media queue: release completes before a replacement is created. */
final class AmbientVideo {
    interface Events {void ready();void failed(String reason);void size(int width,int height);default void released(){}}
    private static final HandlerThread THREAD;private static final Handler MEDIA;
    private static final ExecutorService CANCEL=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"AmbientCancel");t.setDaemon(true);return t;});
    private static final AtomicInteger IDS=new AtomicInteger();private static AmbientVideo live;
    static {THREAD=new HandlerThread("AmbientMedia");THREAD.start();MEDIA=new Handler(THREAD.getLooper());}
    private final Context context;private final Events events;private final Handler main=new Handler(Looper.getMainLooper());
    private MediaPlayer player;private volatile RangeMediaSource source;private volatile boolean stopped;
    private boolean requested,rendered,buffering;private long requestedAt,started,progressAt,bufferingAt;private int lastPosition;private boolean progressLogged;
    private final boolean wifiOnly;private final int maxWidth,minutes,id=IDS.incrementAndGet();
    AmbientVideo(Context c,Events e,boolean wifi,int width,int minutes){context=c.getApplicationContext();events=e;wifiOnly=wifi;maxWidth=width;this.minutes=minutes;}
    static boolean wifi(Context context){try{ConnectivityManager m=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);Network n=m.getActiveNetwork();NetworkCapabilities cap=n==null?null:m.getNetworkCapabilities(n);return cap!=null&&cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)&&cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);}catch(Exception e){return false;}}
    private void log(String state){android.util.Log.i("CasaAmbient","session="+id+" state="+state+" elapsed="+(SystemClock.elapsedRealtime()-requestedAt)+" bytes="+(source==null?0:source.bytes()));}
    void start(String url,Surface surface,long budget){
        if(requested||stopped)return;requested=true;requestedAt=SystemClock.elapsedRealtime();
        if(wifiOnly&&!wifi(context)){fail("Vídeo omitido: Solo Wi-Fi está activo");return;}
        if(live!=null&&live!=this){AmbientVideo previous=live;previous.stop();previous.events.failed("Otro ambiente está usando el vídeo; se muestran imágenes");}live=this;
        main.postDelayed(deadline,15000);log("PREPARING");
        MEDIA.post(()->{if(stopped)return;try{
            RangeMediaSource data=new RangeMediaSource(url,budget);data.trace(metadata->android.util.Log.i("CasaAmbient","session="+id+" "+metadata));source=data;if(stopped){CANCEL.execute(data::close);return;}
            player=new MediaPlayer();log("CREATED");player.setVolume(0f,0f);player.setSurface(surface);player.setLooping(true);
            player.setOnPreparedListener(p->{if(stopped)return;try{p.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);p.setVolume(0f,0f);p.start();started=SystemClock.elapsedRealtime();progressAt=started;log("PREPARED");MEDIA.postDelayed(monitor,1000);}catch(Exception e){fail("No se pudo iniciar el vídeo");}});
            player.setOnInfoListener((p,what,extra)->{if(stopped)return true;if(what==MediaPlayer.MEDIA_INFO_BUFFERING_START){buffering=true;bufferingAt=SystemClock.elapsedRealtime();log("BUFFERING");}else if(what==MediaPlayer.MEDIA_INFO_BUFFERING_END){buffering=false;progressAt=SystemClock.elapsedRealtime();log("BUFFERED");}else if(what==MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){rendered=true;log("FIRST_FRAME");main.post(()->{if(!stopped){main.removeCallbacks(deadline);events.ready();}});}return false;});
            player.setOnVideoSizeChangedListener((p,w,h)->{if(stopped)return;if(w>maxWidth||h>maxWidth*9/16+8){fail("El vídeo supera la calidad elegida");return;}main.post(()->{if(!stopped)events.size(w,h);});});
            player.setOnErrorListener((p,what,extra)->{String kind=source==null?"NONE":source.failureKind();log("ERROR_"+what+"_"+extra+"_"+kind);fail(kind.equals("QUOTA")?"Se alcanzó el límite de datos de vídeo":kind.equals("IDENTITY")?"La fuente de vídeo cambió o no permite búsquedas verificadas":kind.equals("TRUNCATED")?"La descarga del vídeo llegó incompleta":"Vídeo detenido por error de formato o red");return true;});
            player.setDataSource(data);if(!stopped)player.prepareAsync();
        }catch(Exception e){log("PREPARE_ERROR_"+e.getClass().getSimpleName());fail("No se pudo preparar el vídeo; se muestran imágenes");}});
    }
    private final Runnable deadline=()->{if(!stopped){log("TOTAL_DEADLINE");fail("La preparación del vídeo superó 15 segundos");}};
    private final Runnable monitor=new Runnable(){public void run(){if(stopped)return;try{
        if(wifiOnly&&!wifi(context)){fail("Vídeo detenido al salir de Wi-Fi");return;}
        long now=SystemClock.elapsedRealtime();int position=player==null?0:player.getCurrentPosition();
        if(position!=lastPosition){lastPosition=position;progressAt=now;}
        if(buffering&&now-bufferingAt>15000){log("BUFFERING_TIMEOUT");fail("La red no pudo mantener el vídeo");return;}
        if(rendered&&!buffering&&now-progressAt>15000){log("PROGRESS_TIMEOUT");fail("El vídeo dejó de avanzar; se muestran imágenes");return;}
        if(!progressLogged&&now-started>10000){progressLogged=true;log("PROGRESS_"+position);}
        if(now-started>=minutes*60000L){log("TIME_LIMIT");fail("Se alcanzó el tiempo de vídeo elegido");return;}
        MEDIA.postDelayed(this,1000);
    }catch(Exception e){fail("El reproductor no pudo continuar");}}};
    private void fail(String reason){main.post(()->{if(stopped)return;stop();events.failed(reason);});}
    void stop(){
        if(stopped)return;stopped=true;main.removeCallbacksAndMessages(null);MEDIA.removeCallbacks(monitor);
        RangeMediaSource data=source;if(data!=null){data.requestClose();CANCEL.execute(data::close);}
        MEDIA.post(()->{try{if(player!=null){player.setOnErrorListener(null);player.setOnPreparedListener(null);player.setOnInfoListener(null);player.setOnVideoSizeChangedListener(null);player.release();player=null;}}finally{RangeMediaSource s=source;if(s!=null){s.requestClose();CANCEL.execute(s::close);s.clearCache();}log("RELEASED");main.post(()->{if(live==this)live=null;events.released();});}});
    }
}
