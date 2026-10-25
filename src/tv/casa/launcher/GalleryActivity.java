package tv.casa.launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.util.concurrent.*;

/** Local gallery controls. Opening this screen never contacts GitHub. */
public final class GalleryActivity extends Activity {
    private RemoteGallery gallery;private ExecutorService worker;private Future<?> task;
    private TextView status;private Button download,toggle,clear;private boolean visible,busy;
    @Override public void onCreate(Bundle state){super.onCreate(state);gallery=new RemoteGallery(this);worker=Executors.newSingleThreadExecutor();getWindow().getDecorView().setSystemUiVisibility(5894);
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setGravity(Gravity.CENTER_VERTICAL);int pad=Math.round(getResources().getDisplayMetrics().widthPixels*0.10f);box.setPadding(pad,24,pad,24);box.setBackgroundColor(0xFF171B20);
        TextView title=new TextView(this);title.setText("Galería de fondos");title.setTextSize(30);title.setTextColor(MainActivity.INK);title.setTypeface(Typeface.create("serif",0));box.addView(title);
        TextView detail=new TextView(this);detail.setText("Solo se conecta a GitHub al descargar. Máximo 3 imágenes, 12 MiB por pack.\nGitHub recibe tu IP al conectarse. Sin cuentas ni envío de favoritas.\nLos tres fondos actuales ya están incluidos; futuras imágenes podrán llegar sin actualizar la APK.");detail.setTextColor(MainActivity.MUTED);detail.setTextSize(16);box.addView(detail);
        status=new TextView(this);status.setTextColor(MainActivity.INK);status.setTextSize(17);status.setPadding(0,18,0,12);box.addView(status);
        download=button(box,"Descargar o actualizar desde GitHub",()->start(true));
        toggle=button(box,"",()->{if(gallery.local().length==0){status.setText("Descarga un pack antes de activarlo.");return;}boolean enabled=Configuration.prefs(this).getBoolean("ambientRemote",false);Configuration.prefs(this).edit().putBoolean("ambientRemote",!enabled).apply();refresh();});
        clear=button(box,"Borrar fondos descargados",()->start(false));
        button(box,"Ver ambiente",()->{if(!busy)startActivity(new Intent(this,AmbientActivity.class));});
        button(box,"Volver",this::finish);setContentView(box);download.requestFocus();refresh();
    }
    private Button button(LinearLayout box,String label,Runnable action){Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setTextColor(MainActivity.INK);b.setTextSize(17);b.setPadding(12,0,12,0);b.setIncludeFontPadding(false);b.setGravity(Gravity.CENTER);b.setMinHeight(0);b.setMinimumHeight(0);
        android.graphics.drawable.StateListDrawable states=new android.graphics.drawable.StateListDrawable();for(boolean focus:new boolean[]{true,false}){GradientDrawable shape=new GradientDrawable();shape.setColor(focus?0xFF514C43:0xFF2D333B);shape.setCornerRadius(18);shape.setStroke(2,focus?0xFFFFE8C5:0xFF777E85);states.addState(focus?new int[]{android.R.attr.state_focused}:new int[]{},shape);}b.setBackground(states);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,Math.round(48*getResources().getDisplayMetrics().density));lp.topMargin=10;box.addView(b,lp);b.setOnClickListener(v->action.run());return b;}
    private void refresh(){int count=gallery.local().length;status.setText(count==0?"Sin descargas · ambiente integrado disponible.":count+" fondos descargados · disponibles sin conexión.");toggle.setText(Configuration.prefs(this).getBoolean("ambientRemote",false)?"Usar fondos integrados":"Usar fondos descargados");}
    private void start(boolean fetch){if(busy)return;busy=true;download.setEnabled(false);toggle.setEnabled(false);clear.setEnabled(false);status.setText(fetch?"Descargando y comprobando imágenes… Puedes salir para cancelar.":"Borrando descargas…");task=worker.submit(()->{String message;try{if(fetch){int n=gallery.download();message=n+" fondos listos. Puedes activarlos con el botón siguiente.";}else{gallery.clear();Configuration.prefs(this).edit().putBoolean("ambientRemote",false).commit();message="Descargas borradas. Se usan los fondos integrados.";}}catch(Exception e){message="No se completó la operación. Se conservan los fondos anteriores. Intenta de nuevo cuando tengas conexión.";}final String result=message;runOnUiThread(()->{busy=false;download.setEnabled(true);toggle.setEnabled(true);clear.setEnabled(true);if(visible){refresh();status.setText(result);toggle.requestFocus();}});});}
    @Override public void onStart(){super.onStart();visible=true;}
    @Override public void onStop(){visible=false;if(task!=null)task.cancel(true);gallery.cancel();super.onStop();}
    @Override public void onDestroy(){worker.shutdownNow();gallery.cancel();super.onDestroy();}
}
