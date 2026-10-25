package tv.casa.launcher;
import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
public class HomeService extends AccessibilityService {
 long lastOpen=0;
 @Override public void onAccessibilityEvent(AccessibilityEvent e){
   if(e.getEventType()!=AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || e.getClassName()==null)return;
   String cls=e.getClassName().toString();
   if(!cls.equals("com.google.android.apps.tv.launcherx.home.HomeActivity") && !cls.equals("com.spocky.projengmenu.ui.home.MainActivity"))return;
   if(System.currentTimeMillis()<getSharedPreferences("navigation",0).getLong("bypassUntil",0))return;
   long now=SystemClock.elapsedRealtime();if(now-lastOpen<1200)return;lastOpen=now;
   startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
 }
 @Override public void onInterrupt(){}
}
