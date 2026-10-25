package tv.casa.launcher;

import android.app.Activity;
import android.content.*;
import android.graphics.Typeface;
import android.graphics.drawable.*;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.util.concurrent.*;

/** Explicit user control only. No automatic image changes on open, boot or app switching. */
public final class PictureControlsActivity extends Activity {
    private ExecutorService worker;private TclPictureBridge bridge;private TclPictureBridge.State state;
    private TextView status;private Button permission,toggle,undo,refresh;
    private boolean busy,destroyed;
    @Override public void onCreate(Bundle saved){super.onCreate(saved);worker=Executors.newSingleThreadExecutor();
        if(Build.VERSION.SDK_INT>=28)bridge=new TclPictureBridge(this);
        getWindow().getDecorView().setSystemUiVisibility(5894);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setBackgroundColor(0xFF020509);
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setGravity(Gravity.CENTER_VERTICAL);int pad=Math.round(getResources().getDisplayMetrics().widthPixels*0.10f);box.setPadding(pad,24,pad,24);scroll.addView(box);
        TextView title=new TextView(this);title.setText("Imagen del televisor");title.setTextSize(30);title.setTypeface(Typeface.create("sans-serif-light",0));title.setTextColor(MainActivity.INK);box.addView(title);
        TextView detail=new TextView(this);detail.setText("Contenido adaptable ajusta la imagen mediante el procesamiento original de TCL.\nControl verificado en C645 con V652. Se aplica al contexto que tenga activo el TV.\nCasa no cambia brillo, HDR ni calibración desde esta pantalla.");detail.setTextSize(16);detail.setTextColor(MainActivity.MUTED);box.addView(detail);
        status=new TextView(this);status.setTextSize(18);status.setTextColor(MainActivity.INK);status.setPadding(0,16,0,10);box.addView(status);
        permission=button(box,"Permitir controles de TCL",this::requestPermission);
        toggle=button(box,"Contenido adaptable",()->change(false));
        undo=button(box,"Deshacer último cambio de Casa",()->change(true));
        refresh=button(box,"Actualizar estado",this::load);
        button(box,"Abrir ajustes originales de TCL",()->{try{startActivity(new Intent(Settings.ACTION_SETTINGS));}catch(Exception e){status.setText("No se pudieron abrir los ajustes del televisor.");}});
        button(box,"Volver",this::finish);setContentView(scroll);refresh.requestFocus();
    }
    private Button button(LinearLayout box,String text,Runnable action){Button b=new Button(this);b.setText(text);b.setAllCaps(false);b.setTextSize(17);b.setTextColor(MainActivity.INK);b.setPadding(12,0,12,0);b.setIncludeFontPadding(false);b.setMinHeight(0);b.setMinimumHeight(0);b.setGravity(Gravity.CENTER);
        b.setBackground(AuraTheme.states(this,18,AuraTheme.ACCENT));
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,Math.round(44*getResources().getDisplayMetrics().density));lp.topMargin=8;box.addView(b,lp);b.setOnClickListener(v->action.run());return b;
    }
    private void controls(){
        boolean allowed=state!=null&&state.enabled!=null;
        permission.setVisibility(state!=null&&state.permission!=null?View.VISIBLE:View.GONE);permission.setEnabled(!busy);
        toggle.setEnabled(!busy&&allowed);toggle.setText(allowed?(state.enabled?"Desactivar contenido adaptable":"Activar contenido adaptable"):"Contenido adaptable · sin acceso");
        SharedPreferences p=getSharedPreferences("TclPictureUndo",0);undo.setEnabled(!busy&&allowed&&p.contains("previous")&&state.enabled.booleanValue()!=p.getBoolean("previous",false));refresh.setEnabled(!busy);toggle.setAlpha(toggle.isEnabled()?1f:0.4f);undo.setAlpha(undo.isEnabled()?1f:0.4f);
    }
    private void load(){if(busy)return;busy=true;controls();status.setText("Consultando el estado del televisor…");worker.execute(()->{TclPictureBridge.State result=null;String error=null;try{if(bridge==null)throw new IllegalStateException();result=bridge.read();}catch(Exception e){error="Este control no está disponible. Puedes usar los ajustes originales de TCL.";}final TclPictureBridge.State next=result;final String failure=error;runOnUiThread(()->{if(destroyed)return;state=next;busy=false;controls();status.setText(failure!=null?failure:next.enabled==null?"Permite mostrar controles de Configuración. No marques acceso a cualquier aplicación.":"Contenido adaptable: "+(next.enabled?"activado":"desactivado"));if(next!=null&&next.permission!=null)permission.requestFocus();else if(next!=null&&next.enabled!=null)toggle.requestFocus();else refresh.requestFocus();});});}
    private void requestPermission(){if(busy||bridge==null)return;busy=true;controls();worker.execute(()->{try{TclPictureBridge.State latest=bridge.read();runOnUiThread(()->{if(destroyed)return;busy=false;try{if(latest.permission!=null)latest.permission.send();else load();}catch(Exception e){status.setText("No se pudo abrir el permiso de Android.");}controls();});}catch(Exception e){runOnUiThread(()->{if(destroyed)return;busy=false;controls();status.setText("No se pudo consultar el permiso de TCL.");});}});}
    private void change(boolean restoring){if(busy||state==null||state.enabled==null)return;boolean desired=restoring?getSharedPreferences("TclPictureUndo",0).getBoolean("previous",state.enabled):!state.enabled;busy=true;controls();status.setText(restoring?"Restaurando el estado guardado…":"Aplicando y comprobando el cambio…");worker.execute(()->{TclPictureBridge.State result=null;boolean success=false;try{bridge.set(desired,!restoring);result=bridge.read();success=true;}catch(Exception e){try{result=bridge.read();}catch(Exception ignored){}}final TclPictureBridge.State latest=result;final boolean confirmed=success;runOnUiThread(()->{if(destroyed)return;state=confirmed?latest:null;busy=false;controls();if(confirmed)toggle.requestFocus();else refresh.requestFocus();status.setText(confirmed?"Contenido adaptable: "+(desired?"activado":"desactivado")+(restoring?" · restaurado":" · confirmado"):"No se pudo confirmar el cambio. Actualiza el estado antes de volver a intentarlo.");});});}
    @Override public void onResume(){super.onResume();if(worker!=null)load();}
    @Override public void onDestroy(){destroyed=true;worker.shutdown();super.onDestroy();}
}
