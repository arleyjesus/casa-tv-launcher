package tv.casa.launcher;
import android.service.dreams.DreamService;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.os.*;
import java.text.*;
import java.util.*;
public class CasaDream extends DreamService {
  Handler h=new Handler(); TextView clock; int minute=0;
  Runnable tick=new Runnable(){public void run(){if(clock==null)return;clock.setText(new SimpleDateFormat("HH:mm\nEEEE, d 'de' MMMM",new Locale("es")).format(new Date()));clock.setTranslationX((minute%3-1)*12);clock.setTranslationY((minute%2)*8);minute++;h.postDelayed(this,60000);}};
  @Override public void onAttachedToWindow(){super.onAttachedToWindow();setInteractive(false);setFullscreen(true);setScreenBright(false);FrameLayout root=new FrameLayout(this);root.addView(MainActivity.background(this),new FrameLayout.LayoutParams(-1,-1));clock=new TextView(this);clock.setTextColor(MainActivity.INK);clock.setTextSize(27);clock.setTypeface(Typeface.create("serif",0));clock.setGravity(Gravity.RIGHT);clock.setLineSpacing(8,1.1f);FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(-2,-2,Gravity.BOTTOM|Gravity.RIGHT);p.setMargins(60,40,70,65);root.addView(clock,p);setContentView(root);tick.run();}
  @Override public void onDetachedFromWindow(){h.removeCallbacksAndMessages(null);clock=null;super.onDetachedFromWindow();}
}
