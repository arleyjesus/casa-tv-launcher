package tv.casa.launcher;
import android.app.Activity;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.Typeface;
import java.text.SimpleDateFormat;
import java.util.*;
public class AmbientActivity extends Activity {
 AmbientScene scene;
 public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894);scene=new AmbientScene(this,getIntent().getBooleanExtra("video",false));scene.setOnClickListener(v->finish());setContentView(scene);}
 public void onResume(){super.onResume();scene.start();}
 public void onPause(){scene.stop();super.onPause();}
 public boolean onKeyDown(int code,KeyEvent e){if(code==KeyEvent.KEYCODE_DPAD_LEFT||code==KeyEvent.KEYCODE_DPAD_RIGHT||code==KeyEvent.KEYCODE_DPAD_UP){if(e.getRepeatCount()==0)scene.controls(code==KeyEvent.KEYCODE_DPAD_LEFT?-1:code==KeyEvent.KEYCODE_DPAD_RIGHT?1:0);return true;}return super.onKeyDown(code,e);}
 public boolean onKeyUp(int code,KeyEvent e){if(code==KeyEvent.KEYCODE_DPAD_CENTER||code==KeyEvent.KEYCODE_BACK||code==KeyEvent.KEYCODE_ENTER){finish();return true;}return super.onKeyUp(code,e);}
}
