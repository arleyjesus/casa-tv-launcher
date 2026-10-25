package tv.casa.launcher;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;

/** Small native UI shared only by game screens. */
abstract class GameUi extends Activity {
    float scale;
    int px(float n){return Math.round(n*scale);}
    void fullscreen(){getWindow().getDecorView().setSystemUiVisibility(5894);}
    TextView text(String value,int size,int color){TextView t=new TextView(this);t.setText(value);t.setTextColor(color);t.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,px(size));t.setIncludeFontPadding(false);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    TextView button(String value,Runnable action){TextView b=text(value,14,AuraTheme.INK);b.setPadding(px(14),px(8),px(14),px(8));b.setGravity(Gravity.CENTER);b.setFocusable(true);b.setFocusableInTouchMode(true);b.setId(View.generateViewId());b.setBackground(AuraTheme.states(this,px(14),AuraTheme.GAME));b.setOnClickListener(v->action.run());return b;}
    void section(LinearLayout box,String title){TextView t=text(title,17,AuraTheme.GAME);t.setTypeface(Typeface.create("sans-serif-medium",0));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,px(36));p.topMargin=px(10);box.addView(t,p);}
    void entry(LinearLayout box,String title,Runnable action){TextView b=button(title,action);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,px(46));p.bottomMargin=px(8);box.addView(b,p);}
    void open(android.content.Intent intent){try{startActivity(intent);}catch(Exception e){Toast.makeText(this,"Este acceso no está disponible",0).show();}}
    public void onWindowFocusChanged(boolean has){super.onWindowFocusChanged(has);if(has)fullscreen();}
}
