package tv.casa.launcher;
import android.service.dreams.DreamService;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.os.*;
import java.text.*;
import java.util.*;
public class CasaDream extends DreamService {
  AmbientScene scene;
  @Override public void onAttachedToWindow(){super.onAttachedToWindow();setInteractive(false);setFullscreen(true);setScreenBright(false);scene=new AmbientScene(this);setContentView(scene);}
  @Override public void onDreamingStarted(){super.onDreamingStarted();if(scene!=null)scene.start();}
  @Override public void onDreamingStopped(){if(scene!=null)scene.stop();super.onDreamingStopped();}
  @Override public void onDetachedFromWindow(){if(scene!=null)scene.stop();scene=null;super.onDetachedFromWindow();}
}
