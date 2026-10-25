package tv.casa.launcher;
import android.app.Activity;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.graphics.Typeface;
import java.text.SimpleDateFormat;
import java.util.*;
public class AmbientActivity extends Activity {
 Handler handler=new Handler();TextView clock;
 Runnable tick=new Runnable(){public void run(){clock.setText(new SimpleDateFormat("HH:mm\nEEEE, d 'de' MMMM",new Locale("es")).format(new Date()));handler.postDelayed(this,60000);}};
 public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894);FrameLayout root=new FrameLayout(this);root.addView(MainActivity.background(this),new FrameLayout.LayoutParams(-1,-1));clock=new TextView(this);clock.setTextSize(27);clock.setTextColor(MainActivity.INK);clock.setGravity(Gravity.RIGHT);clock.setTypeface(Typeface.create("serif",0));clock.setLineSpacing(8,1.1f);FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.RIGHT);lp.setMargins(50,40,70,65);root.addView(clock,lp);root.setOnClickListener(v->finish());setContentView(root);}
 public void onResume(){super.onResume();tick.run();}
 public void onPause(){handler.removeCallbacksAndMessages(null);super.onPause();}
 public boolean onKeyUp(int code,KeyEvent e){if(code==KeyEvent.KEYCODE_DPAD_CENTER||code==KeyEvent.KEYCODE_BACK||code==KeyEvent.KEYCODE_ENTER){finish();return true;}return super.onKeyUp(code,e);}
}
