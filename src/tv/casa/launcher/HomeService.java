package tv.casa.launcher;
import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.SystemClock;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
public class HomeService extends AccessibilityService {
 private final HomeRedirectPolicy policy = new HomeRedirectPolicy();
 @Override protected void onServiceConnected(){
   AccessibilityServiceInfo info=getServiceInfo();
   if(info!=null){info.notificationTimeout=0;setServiceInfo(info);}
   getSharedPreferences("navigation",0);
 }
 @Override public void onAccessibilityEvent(AccessibilityEvent e){
   if(e.getEventType()!=AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || e.getClassName()==null || e.getPackageName()==null)return;
   String cls=e.getClassName().toString();
   if(!HomeRedirectPolicy.isHome(e.getPackageName().toString(),cls))return;
   if(System.currentTimeMillis()<getSharedPreferences("navigation",0).getLong("bypassUntil",0))return;
   if(!policy.accept(SystemClock.uptimeMillis()))return;
   HomeTiming.requested(e.getEventTime());
   startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION));
 }
 @Override public void onInterrupt(){}
}
