package tv.casa.launcher.check;
import android.app.*;import android.content.*;import android.os.*;import android.view.*;import android.widget.*;import java.lang.reflect.*;import java.util.concurrent.atomic.*;
public final class Checks extends Instrumentation {
    StringBuilder report=new StringBuilder();
    interface Condition {boolean ready()throws Exception;}
    interface Action {void run()throws Exception;}
    void ui(Action a)throws Exception{AtomicReference<Throwable> error=new AtomicReference<>();runOnMainSync(()->{try{a.run();}catch(Throwable e){error.set(e);}});if(error.get()!=null)throw new Exception(error.get());}
    Object field(Object object,String name)throws Exception{Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);return f.get(object);}
    void set(Object object,String name,Object value)throws Exception{Field f=object.getClass().getDeclaredField(name);f.setAccessible(true);f.set(object,value);}
    void invoke(Object object,String name)throws Exception{Method m=object.getClass().getDeclaredMethod(name);m.setAccessible(true);m.invoke(object);}
    Object onUi(Object object,String name)throws Exception{AtomicReference<Object> out=new AtomicReference<>();ui(()->out.set(field(object,name)));return out.get();}
    void waitFor(String name,Condition c,long limit)throws Exception{long end=SystemClock.elapsedRealtime()+limit;while(SystemClock.elapsedRealtime()<end){if(c.ready())return;Thread.sleep(50);}throw new Exception("Timeout: "+name);}
    void check(boolean b,String message)throws Exception{if(!b)throw new Exception(message);}
    void passed(String s){report.append(s).append('\n');android.util.Log.i("VeliraChecks",s);}
    Activity open(String cls,boolean video){Intent i=new Intent().setClassName("tv.casa.launcher","tv.casa.launcher."+cls).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);if(video)i.putExtra("video",true);return startActivitySync(i);}
    void close(Activity a)throws Exception{ui(a::finish);waitForIdleSync();Thread.sleep(600);}
    String state(Object scene)throws Exception{Object state=onUi(scene,"playback");AtomicReference<String> result=new AtomicReference<>();ui(()->{Method m=state.getClass().getDeclaredMethod("state");m.setAccessible(true);result.set(String.valueOf(m.invoke(state)));});return result.get();}
    private String mode="full";
    public void onCreate(Bundle arguments){super.onCreate(arguments);if(arguments!=null)mode=arguments.getString("mode","full");start();}
    private void deadline()throws Exception{
        Class<?> videoClass=Class.forName("tv.casa.launcher.AmbientVideo",true,getTargetContext().getClassLoader());Field mediaField=videoClass.getDeclaredField("MEDIA");mediaField.setAccessible(true);Handler media=(Handler)mediaField.get(null);java.util.concurrent.CountDownLatch blocked=new java.util.concurrent.CountDownLatch(1),unblock=new java.util.concurrent.CountDownLatch(1);
        media.post(()->{blocked.countDown();try{unblock.await();}catch(InterruptedException ignored){}});check(blocked.await(3,java.util.concurrent.TimeUnit.SECONDS),"media barrier did not start");Activity timed=null;
        try{timed=open("AmbientActivity",true);Object timedScene=onUi(timed,"scene");waitFor("preparing behind media barrier",()->state(timedScene).equals("PREPARING"),5000);long began=SystemClock.elapsedRealtime();waitFor("independent total deadline",()->state(timedScene).equals("FALLBACK"),18000);long elapsed=SystemClock.elapsedRealtime()-began;check(elapsed>=14000&&elapsed<=18000,"unexpected total deadline timing "+elapsed);ui(()->check(((View)field(timedScene,"pictures")).getVisibility()==View.VISIBLE,"poster absent after deadline"));}finally{unblock.countDown();if(timed!=null)close(timed);}
        passed("PASS: independent 15-second deadline recovers photos while the media queue is deliberately blocked");
    }

    public void onStart(){try{android.util.Log.i("VeliraChecks","START regression build011");
        if(mode.equals("deadline")){deadline();Bundle result=new Bundle();result.putString("stream",report.toString());finish(Activity.RESULT_OK,result);return;}
        Activity panel=open("OnlineAmbientActivity",false);
        waitFor("initial snapshot",()->Boolean.TRUE.equals(onUi(panel,"snapshotReady")),8000);
        ui(()->{set(panel,"tab",3);invoke(panel,"show");});
        Class<?> job=Class.forName("tv.casa.launcher.OnlineAmbientActivity$Job",true,getTargetContext().getClassLoader());
        AtomicBoolean interrupted=new AtomicBoolean();
        Object operation=Proxy.newProxyInstance(job.getClassLoader(),new Class[]{job},(proxy,method,args)->{if(method.getName().equals("run")){try{Thread.sleep(5000);}catch(InterruptedException e){interrupted.set(true);throw e;}return "Unexpected completion";}return null;});
        ui(()->{Method run=panel.getClass().getDeclaredMethod("run",String.class,job);run.setAccessible(true);run.invoke(panel,"Prueba de cancelación",operation);check(!panel.findViewById(android.R.id.content).findViewWithTag("refresh").isEnabled(),"busy row remained enabled");});
        Thread.sleep(200);
        // Controlled same-instance lifecycle; no network or preferences are changed.
        ui(()->{callActivityOnStop(panel);callActivityOnStart(panel);});
        waitFor("snapshot after cancellation",()->Boolean.TRUE.equals(onUi(panel,"snapshotReady")),8000);
        ui(()->{View row=panel.findViewById(android.R.id.content).findViewWithTag("refresh");check(row.isEnabled(),"row disabled after return");check(((Integer)field(panel,"tab"))==3,"tab lost");check(String.valueOf(field(panel,"message")).contains("cancelada"),"cancellation message lost");});
        check(interrupted.get(),"job did not receive cancellation");passed("PASS: cancelled job, same-instance return, enabled controls, retained tab, no automatic restart");close(panel);
        Activity photos=open("AmbientActivity",false);Object photoScene=onUi(photos,"scene");
        java.io.File damaged=new java.io.File(getTargetContext().getCacheDir(),"velira-corrupt-check.img");
        try{try(java.io.FileOutputStream out=new java.io.FileOutputStream(damaged)){out.write(new byte[]{1,2,3});}
            Class<?> frameType=Class.forName("tv.casa.launcher.AmbientScene$Frame",true,getTargetContext().getClassLoader());java.lang.reflect.Constructor<?> constructor=frameType.getDeclaredConstructor();constructor.setAccessible(true);Object frame=constructor.newInstance();set(frame,"file",damaged);set(frame,"fallback",0);set(frame,"caption","Incorrect external credit");set(frame,"sha","0000000000000000000000000000000000000000000000000000000000000000");
            Method decode=photoScene.getClass().getDeclaredMethod("decode",frameType);decode.setAccessible(true);Object decoded=decode.invoke(photoScene,frame);check(field(decoded,"bitmap")!=null,"corrupt image has no fallback");check(String.valueOf(field(decoded,"caption")).contains("Paisaje integrado"),"corrupt image retained wrong credit");
        }finally{damaged.delete();close(photos);}
        passed("PASS: corrupt local image falls back to a built-in photo with the correct credit");
        Class<?> storeClass=Class.forName("tv.casa.launcher.OnlineAmbientStore",true,getTargetContext().getClassLoader());java.lang.reflect.Constructor<?> storeConstructor=storeClass.getDeclaredConstructor(Context.class);storeConstructor.setAccessible(true);Object store=storeConstructor.newInstance(getTargetContext());Method catalogMethod=storeClass.getDeclaredMethod("catalog");catalogMethod.setAccessible(true);Object catalog=catalogMethod.invoke(store);Object collection=((java.util.List<?>)field(catalog,"collections")).get(0);Object item=((java.util.List<?>)field(collection,"items")).get(0);Object variant=((java.util.List<?>)field(item,"variants")).get(0);Object http=field(store,"http");Method download=http.getClass().getDeclaredMethod("download",String.class,java.io.File.class,int.class);download.setAccessible(true);java.io.File part=new java.io.File(getTargetContext().getCacheDir(),"velira-download-check.part");
        try{String hash=(String)download.invoke(http,field(variant,"url"),part,(Integer)field(variant,"bytes"));check(hash.equals(field(variant,"sha")),"real image streaming hash mismatch");check(part.length()==(Integer)field(variant,"bytes"),"real image streaming size mismatch");}finally{part.delete();}
        passed("PASS: real HTTPS image streamed to a temporary file with matching size and incremental SHA256");
        Activity pair=open("AmbientActivity",true);Object firstScene=onUi(pair,"scene");waitFor("first concurrent scene",()->state(firstScene).equals("VIDEO"),22000);
        Class<?> sceneClass=firstScene.getClass();java.lang.reflect.Constructor<?> sceneConstructor=sceneClass.getDeclaredConstructor(Context.class,boolean.class);sceneConstructor.setAccessible(true);AtomicReference<Object> extraScene=new AtomicReference<>();
        ui(()->{Object second=sceneConstructor.newInstance(pair,true);extraScene.set(second);((FrameLayout)pair.findViewById(android.R.id.content)).addView((View)second,new FrameLayout.LayoutParams(300,180));invoke(second,"start");});Object secondScene=extraScene.get();
        try{waitFor("second concurrent scene",()->state(secondScene).equals("VIDEO"),22000);check(state(firstScene).equals("FALLBACK"),"superseded scene did not recover its photo");}finally{ui(()->invoke(secondScene,"stop"));close(pair);}
        passed("PASS: two concurrent scenes hand over the decoder; the replaced scene returns to a photo");
        for(int cycle=0;cycle<20;cycle++){
            Activity preview=open("AmbientActivity",true);Object scene=onUi(preview,"scene");
            if(cycle==0||cycle==10){waitFor("first frame cycle "+cycle,()->state(scene).equals("VIDEO"),22000);ui(()->((View)field(scene,"videoSurface")).setVisibility(View.GONE));waitFor("surface fallback",()->state(scene).equals("FALLBACK"),4000);ui(()->{check(((View)field(scene,"pictures")).getVisibility()==View.VISIBLE,"photos hidden after surface loss");ImageView[] images=(ImageView[])field(scene,"images");check(images[(Integer)field(scene,"front")].getDrawable()!=null,"poster missing after fallback");});}
            else {waitFor("preparing cycle "+cycle,()->state(scene).equals("PREPARING"),8000);Thread.sleep(100);}
            close(preview);check(state(scene).equals("STOPPED"),"scene did not stop");
        }
        passed("PASS: 20 preview entry/exit cycles including prepare cancellation; 2 first-frame and surface-loss recoveries");
        deadline();
        Thread.sleep(1500);getTargetContext().startActivity(new Intent().setClassName("tv.casa.launcher","tv.casa.launcher.MainActivity").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        Bundle result=new Bundle();result.putString("stream",report.toString());finish(Activity.RESULT_OK,result);
    }catch(Throwable e){Bundle result=new Bundle();result.putString("stream",report+"FAIL: "+e.toString());android.util.Log.e("VeliraChecks","Regression failed",e);finish(Activity.RESULT_CANCELED,result);}}
}
