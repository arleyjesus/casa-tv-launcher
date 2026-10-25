package tv.casa.launcher;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.tv.*;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.util.*;
import java.text.*;
import java.io.File;
import java.util.concurrent.*;

public class MainActivity extends Activity {
    static final int INK=Color.rgb(246,237,224), MUTED=Color.rgb(200,186,169), GOLD=Color.rgb(232,203,165);
    FrameLayout root; float scale; TextView clock,hero,headerSettings; Handler handler=new Handler();
    Dialog activeDialog;
    boolean resumed=false,alive=true;
    String lastFocus="";
    final ExecutorService loader=Executors.newSingleThreadExecutor();
    ArrayList<String> appCache;
    final Map<String,String> labelsCache=new ConcurrentHashMap<>();
    final SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm",Locale.getDefault());
    final BroadcastReceiver packageChanges=new BroadcastReceiver(){public void onReceive(Context c,Intent i){appCache=null;labelsCache.clear();Assets.clearIcons();if(root!=null)refreshFavoritesOnly();}};
    final ArrayList<View> favoritesViews=new ArrayList<>(), utilityViews=new ArrayList<>();
    static final String[] DEFAULT_FAVORITES={"com.netflix.ninja","com.google.android.youtube.tv","com.amazon.amazonvideo.livingroom","com.disney.disneyplus","com.wbd.stream","com.spotify.tv.android"};
    final String[] initial=DEFAULT_FAVORITES;
    Runnable ticker=new Runnable(){ public void run(){handler.removeCallbacks(this);if(!resumed||clock==null)return;clock.setText(timeFormat.format(new Date()));handler.postDelayed(this,60000-(System.currentTimeMillis()%60000));}};
    int px(float v){return Math.round(v*scale);}
    @Override public void onCreate(Bundle b){super.onCreate(b);lastFocus=getPreferences(0).getString("lastFocus","");immersive();render();IntentFilter f=new IntentFilter();f.addAction(Intent.ACTION_PACKAGE_ADDED);f.addAction(Intent.ACTION_PACKAGE_REMOVED);f.addAction(Intent.ACTION_PACKAGE_CHANGED);f.addDataScheme("package");registerReceiver(packageChanges,f);}
    @Override public void onNewIntent(Intent i){super.onNewIntent(i);setIntent(i);closePanel();immersive();restoreFocus();}
    @Override public void onWindowFocusChanged(boolean focused){super.onWindowFocusChanged(focused);if(focused)HomeTiming.focused();}
    void immersive(){getWindow().getDecorView().setSystemUiVisibility(5894);}
    @Override public void onResume(){super.onResume();resumed=true;immersive();applyAppearance();ticker.run();}
    @Override public void onPause(){resumed=false;getPreferences(0).edit().putString("lastFocus",lastFocus).apply();handler.removeCallbacks(ticker);super.onPause();}
    @Override public void onDestroy(){alive=false;unregisterReceiver(packageChanges);loader.shutdownNow();handler.removeCallbacksAndMessages(null);super.onDestroy();}
    static ImageView background(Context c){ImageView image=new ImageView(c);image.setScaleType(ImageView.ScaleType.CENTER_CROP);Bitmap bitmap=Assets.landscape(c);if(bitmap!=null)image.setImageBitmap(bitmap);else image.setBackgroundColor(0xFF211B17);return image;}
    TextView text(String value,float size,int color){TextView t=new TextView(this);t.setText(value);t.setTextColor(color);t.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,px(size));t.setGravity(Gravity.CENTER_VERTICAL);t.setIncludeFontPadding(false);return t;}
    void put(View v,float x,float y,float w,float h){FrameLayout.LayoutParams p=new FrameLayout.LayoutParams(px(w),px(h));p.leftMargin=px(x);p.topMargin=px(y);root.addView(v,p);}
    GradientDrawable surface(boolean focused){GradientDrawable g=new GradientDrawable();g.setColor(focused?0xE63A2C22:0xCE211D1A);g.setCornerRadius(px(10));g.setStroke(px(focused?1.3f:0.6f),focused?GOLD:0x664D4138);return g;}
    void focusable(View v){v.setId(View.generateViewId());v.setFocusable(true);v.setFocusableInTouchMode(true);StateListDrawable state=new StateListDrawable();state.addState(new int[]{android.R.attr.state_focused},surface(true));state.addState(new int[]{},surface(false));v.setBackground(state);v.setOnFocusChangeListener((a,f)->{if(f&&a.getTag() instanceof String)lastFocus=(String)a.getTag();a.animate().cancel();boolean animate=getPreferences(0).getBoolean("animateFocus",true) && a.getParent() instanceof FrameLayout;float factor=f&&animate?1.035f:1f;if(animate)a.animate().scaleX(factor).scaleY(factor).setDuration(100).start();else{a.setScaleX(factor);a.setScaleY(factor);}});}
    TextView button(String title,Runnable action){TextView b=text(title,13,INK);b.setGravity(Gravity.CENTER);focusable(b);b.setOnClickListener(v->action.run());return b;}
    ArrayList<String> favoritePackages(){String raw=getPreferences(0).getString("favorites",String.join(",",initial));ArrayList<String> result=new ArrayList<>();for(String p:raw.split(",")){if(launchIntent(p)!=null)result.add(p);}return result;}
    Intent launchIntent(String p){PackageManager pm=getPackageManager();Intent i=pm.getLeanbackLaunchIntentForPackage(p);return i!=null?i:pm.getLaunchIntentForPackage(p);}
    String label(String p){switch(p){case "com.netflix.ninja":return "Netflix";case "com.google.android.youtube.tv":return "YouTube";case "com.amazon.amazonvideo.livingroom":return "Prime Video";case "com.disney.disneyplus":return "Disney+";case "com.wbd.stream":return "Max";case "com.spotify.tv.android":return "Spotify";}String cached=labelsCache.get(p);if(cached!=null)return cached;try{String name=getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(p,0)).toString();labelsCache.put(p,name);return name;}catch(Exception e){return p;}}
    void render(){
      if(root!=null){refreshFavoritesOnly();applyAppearance();return;}
      scale=getResources().getDisplayMetrics().widthPixels/960f;
      root=new FrameLayout(this);root.setBackgroundColor(0xFF161310);setContentView(root);
      root.addView(background(this),new FrameLayout.LayoutParams(-1,-1));favoritesViews.clear();utilityViews.clear();
      TextView brand=text("C A S A",12,INK);put(brand,38,26,160,24);
      clock=text("",15,INK);clock.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);put(clock,758,26,90,24);ticker.run();handler.removeCallbacks(ticker);
      TextView settings=button("⚙",()->showSettings());headerSettings=settings;settings.setTag("settings");settings.setContentDescription("Ajustes rápidos");put(settings,868,20,48,36);
      hero=text("Tu espacio.",49,INK);hero.setTypeface(Typeface.create("serif",Typeface.NORMAL));put(hero,38,240,650,72);
      TextView caption=text("F A V O R I T A S",10,MUTED);put(caption,39,344,250,20);
      ArrayList<String> favorites=favoritePackages();int n=Math.min(6,favorites.size());float cardWidth=(884-14*5)/6f;
      for(int x=0;x<n;x++){
        final String p=favorites.get(x);LinearLayout card=new LinearLayout(this);card.setGravity(Gravity.CENTER_VERTICAL);card.setPadding(px(11),px(8),px(8),px(8));focusable(card);card.setContentDescription(label(p));
        card.setTag(p);ImageView icon=new ImageView(this);icon.setScaleType(ImageView.ScaleType.FIT_CENTER);icon.setImageDrawable(Assets.icon(this,p));card.addView(icon,new LinearLayout.LayoutParams(px(28),px(28)));
        TextView name=text(label(p),13,INK);name.setMaxLines(1);name.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-1,1);tp.leftMargin=px(9);card.addView(name,tp);
        card.setOnClickListener(v->launch(p));card.setOnLongClickListener(v->{showApps(true);return true;});put(card,38+x*(cardWidth+14),373,cardWidth,60);favoritesViews.add(card);
      }
      if(n==0){TextView add=button("Elegir favoritas",()->showApps(true));put(add,38,373,220,60);favoritesViews.add(add);}
      String[] labels={"HDMI 1","HDMI 2","HDMI 3","TV en vivo","Aplicaciones","Ajustes"};
      for(int x=0;x<labels.length;x++){final int index=x;TextView b=button(labels[x],()->{if(index<3)openHdmi(index+1);else if(index==3)launch("com.tcl.tv");else if(index==4)showApps(false);else showSettings();});b.setTag("utility-"+x);put(b,38+x*150,466,134,32);utilityViews.add(b);}
      for(int i=0;i<favoritesViews.size();i++){View v=favoritesViews.get(i);v.setNextFocusUpId(settings.getId());v.setNextFocusDownId(utilityViews.get(Math.min(i,5)).getId());v.setNextFocusLeftId(favoritesViews.get(Math.max(0,i-1)).getId());v.setNextFocusRightId(favoritesViews.get(Math.min(favoritesViews.size()-1,i+1)).getId());}
      for(int i=0;i<utilityViews.size();i++){View v=utilityViews.get(i);v.setNextFocusUpId(favoritesViews.get(Math.min(i,favoritesViews.size()-1)).getId());v.setNextFocusLeftId(utilityViews.get(Math.max(0,i-1)).getId());v.setNextFocusRightId(utilityViews.get(Math.min(5,i+1)).getId());v.setNextFocusDownId(v.getId());}
      settings.setNextFocusDownId(favoritesViews.get(favoritesViews.size()-1).getId());restoreFocus();applyAppearance();
    }
    void launch(String p){Intent i=launchIntent(p);if(i==null){Toast.makeText(this,"Aplicación no disponible",0).show();return;}safeStart(i);}
    void safeStart(Intent i){try{startActivity(i);}catch(Exception e){android.util.Log.w("Casa", "Could not open "+i, e);Toast.makeText(this,"Este acceso no está disponible en el TV",0).show();}}
    void openHdmi(int port){try{TvInputManager m=(TvInputManager)getSystemService(TV_INPUT_SERVICE);for(TvInputInfo info:m.getTvInputList()){String l=String.valueOf(info.loadLabel(this));if(info.getType()==TvInputInfo.TYPE_HDMI && l.replace(" ","").toUpperCase(Locale.ROOT).contains("HDMI"+port)){safeStart(new Intent(Intent.ACTION_VIEW,TvContract.buildChannelUriForPassthroughInput(info.getId())).setPackage("com.tcl.tv"));return;}}}catch(Exception ignored){}Toast.makeText(this,"Abriendo entradas del televisor",0).show();launch("com.tcl.tv");}
    ArrayList<String> apps(){TreeMap<String,String> ordered=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);PackageManager pm=getPackageManager();HashSet<String> seen=new HashSet<>();for(String cat:new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER,Intent.CATEGORY_LAUNCHER}){Intent q=new Intent(Intent.ACTION_MAIN).addCategory(cat);for(ResolveInfo r:pm.queryIntentActivities(q,0)){String p=r.activityInfo.packageName;if(!p.equals(getPackageName())&&seen.add(p))ordered.put(label(p)+"\u0000"+p,p);}}return new ArrayList<>(ordered.values());}
    void closePanel(){if(activeDialog!=null){Dialog old=activeDialog;activeDialog=null;old.dismiss();}}
    Dialog panel(String title){closePanel();Dialog d=new Dialog(this);LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(px(24),px(20),px(24),px(20));box.setBackgroundColor(0xFF211B17);TextView h=text(title,25,INK);box.addView(h,new LinearLayout.LayoutParams(-1,px(54)));ScrollView scroll=new ScrollView(this);scroll.setPadding(0,px(5),0,px(5));scroll.setClipToPadding(false);LinearLayout body=new LinearLayout(this);body.setOrientation(1);scroll.addView(body);box.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));d.setContentView(box);d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);d.getWindow().setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);d.setOnDismissListener(v->{if(activeDialog==d)activeDialog=null;immersive();});d.show();d.getWindow().setLayout(px(440),px(480));d.getWindow().getDecorView().setSystemUiVisibility(5894);activeDialog=d;return d;}
    LinearLayout body(Dialog d){return (LinearLayout)((ScrollView)((LinearLayout)((FrameLayout)d.findViewById(android.R.id.content)).getChildAt(0)).getChildAt(1)).getChildAt(0);}
    void entry(LinearLayout body,String label,Runnable action){TextView b=button(label,action);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,px(43));p.bottomMargin=px(8);body.addView(b,p);}
    void showSettings(){Dialog d=panel("Tu TV, a mano.");LinearLayout b=body(d);
      entry(b,"Editar favoritas",()->{d.dismiss();showApps(true);});
      entry(b,"Ordenar favoritas",()->showOrder());
      entry(b,"Apariencia",()->showAppearance());
      entry(b,"Imagen y sonido · ajustes TCL",()->{d.dismiss();safeStart(new Intent(Settings.ACTION_SETTINGS));});
      entry(b,"Red e Internet",()->{d.dismiss();safeStart(new Intent(Settings.ACTION_WIFI_SETTINGS));});
      entry(b,"Bluetooth",()->{d.dismiss();safeStart(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));});
      entry(b,"Ambiente · vista previa",()->{d.dismiss();safeStart(new Intent(this,AmbientActivity.class));});
      entry(b,"Copias de configuración",()->showBackups());
      entry(b,"Estado de Inicio",()->showHomeStatus());
      entry(b,"Abrir Google TV · temporal",()->{getSharedPreferences("navigation",0).edit().putLong("bypassUntil",System.currentTimeMillis()+60000).apply();d.dismiss();safeStart(new Intent().setComponent(new ComponentName("com.google.android.apps.tv.launcherx","com.google.android.apps.tv.launcherx.home.HomeActivity")));});
      entry(b,"Abrir Projectivy · temporal",()->{getSharedPreferences("navigation",0).edit().putLong("bypassUntil",System.currentTimeMillis()+60000).apply();d.dismiss();launch("com.spocky.projengmenu");});
      entry(b,"Acerca de Casa",()->new AlertDialog.Builder(this).setTitle("Casa · 0.3.0").setMessage("Inicio y ambiente personalizados. Sin anuncios ni permiso de Internet. El servicio de Inicio solo detecta las ventanas de inicio de Google TV y Projectivy; no lee su contenido. Mantiene los ajustes originales de TCL.\n\nMantén OK sobre una favorita para editarla.").setPositiveButton("Cerrar",null).show());
      b.getChildAt(0).requestFocus();
    }
    void showApps(boolean edit){Dialog d=panel(edit?"Tus favoritas · hasta 6":"Aplicaciones");LinearLayout b=body(d);if(appCache!=null){populateApps(d,b,new ArrayList<>(appCache),edit);return;}b.addView(text("Cargando aplicaciones…",14,MUTED));loader.execute(()->{ArrayList<String> found=apps();handler.post(()->{if(!alive)return;appCache=found;if(activeDialog==d&&d.isShowing()){b.removeAllViews();populateApps(d,b,found,edit);}});});}
    void populateApps(Dialog d,LinearLayout b,ArrayList<String> packages,boolean edit){ArrayList<String> selected=favoritePackages();
      if(edit)entry(b,"Guardar y volver",()->{getPreferences(0).edit().putString("favorites",String.join(",",selected)).apply();d.dismiss();render();ticker.run();});
      for(String p:packages){
        if(edit){CheckBox check=new CheckBox(this);check.setText(label(p));check.setTextColor(INK);check.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,px(15));check.setChecked(selected.contains(p));check.setPadding(px(8),0,0,0);check.setButtonTintList(android.content.res.ColorStateList.valueOf(GOLD));check.setOnCheckedChangeListener((v,on)->{if(on){if(selected.size()>=6){check.setChecked(false);Toast.makeText(this,"Elige hasta seis favoritas",0).show();}else selected.add(p);}else selected.remove(p);});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,px(43));b.addView(check,lp);
        }else entry(b,label(p),()->{d.dismiss();launch(p);});
      }if(b.getChildCount()>0)b.getChildAt(0).requestFocus();
    }
    void restoreFocus(){View found=lastFocus.isEmpty()?null:root.findViewWithTag(lastFocus);if(found==null)found=favoritesViews.get(0);found.requestFocus();}
    void applyAppearance(){if(hero!=null)hero.setVisibility(getPreferences(0).getBoolean("showTitle",true)?View.VISIBLE:View.INVISIBLE);if(clock!=null)clock.setVisibility(getPreferences(0).getBoolean("showClock",true)?View.VISIBLE:View.INVISIBLE);}
    void refreshFavoritesOnly(){
      for(View v:favoritesViews)root.removeView(v);favoritesViews.clear();ArrayList<String> packages=favoritePackages();float width=(884-14*5)/6f;
      for(int i=0;i<Math.min(6,packages.size());i++){final String pkg=packages.get(i);LinearLayout card=new LinearLayout(this);card.setGravity(Gravity.CENTER_VERTICAL);card.setPadding(px(11),px(8),px(8),px(8));focusable(card);card.setTag(pkg);card.setContentDescription(label(pkg));ImageView icon=new ImageView(this);icon.setScaleType(ImageView.ScaleType.FIT_CENTER);icon.setImageDrawable(Assets.icon(this,pkg));card.addView(icon,new LinearLayout.LayoutParams(px(28),px(28)));TextView name=text(label(pkg),13,INK);name.setMaxLines(1);name.setEllipsize(android.text.TextUtils.TruncateAt.END);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.leftMargin=px(9);card.addView(name,p);card.setOnClickListener(v->launch(pkg));card.setOnLongClickListener(v->{showApps(true);return true;});put(card,38+i*(width+14),373,width,60);favoritesViews.add(card);}
      if(favoritesViews.isEmpty()){TextView add=button("Elegir favoritas",()->showApps(true));put(add,38,373,220,60);favoritesViews.add(add);}
      for(int i=0;i<favoritesViews.size();i++){View v=favoritesViews.get(i);v.setNextFocusUpId(headerSettings.getId());v.setNextFocusDownId(utilityViews.get(Math.min(i,5)).getId());v.setNextFocusLeftId(favoritesViews.get(Math.max(0,i-1)).getId());v.setNextFocusRightId(favoritesViews.get(Math.min(favoritesViews.size()-1,i+1)).getId());}
      for(int i=0;i<utilityViews.size();i++)utilityViews.get(i).setNextFocusUpId(favoritesViews.get(Math.min(i,favoritesViews.size()-1)).getId());headerSettings.setNextFocusDownId(favoritesViews.get(favoritesViews.size()-1).getId());restoreFocus();
    }
    void showAppearance(){Dialog d=panel("Apariencia");LinearLayout b=body(d);option(b,"Mostrar título","showTitle");option(b,"Mostrar reloj","showClock");option(b,"Animación de selección","animateFocus");entry(b,"Volver",()->showSettings());b.getChildAt(0).requestFocus();}
    void option(LinearLayout list,String label,String key){CheckBox check=new CheckBox(this);check.setText(label);check.setTextColor(INK);check.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,px(15));check.setButtonTintList(android.content.res.ColorStateList.valueOf(GOLD));check.setChecked(getPreferences(0).getBoolean(key,true));check.setOnCheckedChangeListener((v,on)->{getPreferences(0).edit().putBoolean(key,on).apply();applyAppearance();});list.addView(check,new LinearLayout.LayoutParams(-1,px(54)));}
    void showOrder(){Dialog d=panel("Ordenar favoritas");LinearLayout b=body(d);ArrayList<String> selected=favoritePackages();for(int i=0;i<selected.size();i++){final int index=i;entry(b,(i+1)+". "+label(selected.get(i)),()->new AlertDialog.Builder(this).setTitle(label(selected.get(index))).setItems(new String[]{"Mover a la izquierda","Mover a la derecha"},(dialog,direction)->{int target=index+(direction==0?-1:1);if(target>=0&&target<selected.size()){Collections.swap(selected,index,target);getPreferences(0).edit().putString("favorites",String.join(",",selected)).apply();refreshFavoritesOnly();}showOrder();}).show());}entry(b,"Volver",()->showSettings());b.getChildAt(0).requestFocus();}
    void showBackups(){Dialog d=panel("Tu configuración");LinearLayout b=body(d);entry(b,"Crear copia local",()->{try{Configuration.export(this);Toast.makeText(this,"Copia creada",0).show();}catch(Exception e){Toast.makeText(this,"No se pudo crear la copia",0).show();}});entry(b,"Restaurar una copia",()->chooseBackup());entry(b,"Volver",()->showSettings());b.getChildAt(0).requestFocus();}
    void chooseBackup(){try{File[] files=Configuration.copies(this);if(files.length==0){Toast.makeText(this,"Todavía no hay copias",0).show();return;}Dialog d=panel("Restaurar configuración");LinearLayout b=body(d);SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault());for(File file:files)entry(b,format.format(new Date(file.lastModified())),()->new AlertDialog.Builder(this).setTitle("¿Restaurar esta copia?").setMessage("Se restaurarán favoritas y apariencia. Antes se guardará una copia del estado actual.").setPositiveButton("Restaurar",(a,c)->{try{Configuration.restore(this,file);render();closePanel();restoreFocus();Toast.makeText(this,"Configuración restaurada",0).show();}catch(Exception e){Toast.makeText(this,"La copia no es válida o no se pudo restaurar",0).show();}}).setNegativeButton("Cancelar",null).show());b.getChildAt(0).requestFocus();}catch(Exception e){Toast.makeText(this,"No se pudieron leer las copias",0).show();}}
    void showHomeStatus(){int enabled=Settings.Secure.getInt(getContentResolver(),Settings.Secure.ACCESSIBILITY_ENABLED,0);String services=Settings.Secure.getString(getContentResolver(),Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);boolean selected=services!=null&&(services.contains("tv.casa.launcher/.HomeService")||services.contains("tv.casa.launcher/tv.casa.launcher.HomeService"));new AlertDialog.Builder(this).setTitle("Botón Inicio").setMessage(enabled==1&&selected?"Casa está seleccionada en Accesibilidad. Si Home no responde, desactiva y activa Casa · Botón Inicio en los ajustes del TV.":"La redirección está desactivada. Puedes activar Casa · Botón Inicio en Accesibilidad.").setPositiveButton("Abrir Accesibilidad",(a,b)->{closePanel();safeStart(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));}).setNegativeButton("Cerrar",null).show();}
    @Override public boolean onKeyUp(int key,KeyEvent e){if(key==KeyEvent.KEYCODE_MENU){showSettings();return true;}return super.onKeyUp(key,e);}
    @Override public void onBackPressed(){showSettings();}
}
