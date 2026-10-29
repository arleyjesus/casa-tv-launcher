package tv.casa.launcher;

import android.graphics.Typeface;
import android.text.*;
import android.text.style.*;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

/** Native layouts sharing a restrained blue dock and explicit remote navigation. */
final class HomeLayouts {
    static void create(MainActivity a){
        android.util.DisplayMetrics dm=a.getResources().getDisplayMetrics();
        a.scale=Math.min(dm.widthPixels/960f,dm.heightPixels/540f);
        a.insetX=(dm.widthPixels-a.px(960))/2;a.insetY=(dm.heightPixels-a.px(540))/2;
        a.root=new FrameLayout(a);a.root.setBackgroundColor(0xFF020D20);a.setContentView(a.root);
        a.root.addView(MainActivity.background(a),new FrameLayout.LayoutParams(-1,-1));
        a.favoritesViews.clear();a.utilityViews.clear();a.headerViews.clear();
        String preset=a.preset();boolean classic=preset.equals("classic"),immersive=preset.equals("immersive");

        a.root.addView(new HomeShade(a),new FrameLayout.LayoutParams(-1,-1));header(a,!classic&&!immersive);
        ArrayList<String> packages=a.favoritePackages();
        if(classic)classic(a,packages);else if(immersive)immersive(a,packages);else modern(a,packages);
        wire(a,preset);a.restoreFocus();a.applyAppearance();updateClock(a);
    }
    static void header(MainActivity a,boolean tabs){
        SpannableString brand=new SpannableString("Velira TV");brand.setSpan(new ForegroundColorSpan(0xFF25B8FF),7,9,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView name=a.text("",24,MainActivity.INK);name.setText(brand);name.setTypeface(Typeface.create("sans-serif-medium",0));a.put(name,36,25,145,36);
        if(tabs){String[] labels={"Inicio","Apps","Juegos","Ambiente"};Runnable[] actions={()->{if(!a.favoritesViews.isEmpty())a.favoritesViews.get(0).requestFocus();},a::showLibrary,a::showGaming,()->a.safeStart(new android.content.Intent(a,AmbientActivity.class))};for(int i=0;i<labels.length;i++){TextView b=a.button(labels[i],actions[i]);b.setTag("nav-"+i);a.put(b,196+i*94,28,86,31);a.headerViews.add(b);}}
        FrameLayout b=new FrameLayout(a);a.focusable(b);b.setBackground(AuraTheme.states(a,a.px(22),MainActivity.GOLD));b.setTag("settings");b.setContentDescription("Ajustes");b.setOnClickListener(v->a.showSettings());b.addView(new HomeGlyph(a,"settings"),new FrameLayout.LayoutParams(-1,-1));a.put(b,884,22,40,40);a.headerViews.add(b);a.headerSettings=b;
    }
    static FrameLayout dock(MainActivity a,float x,float y,float w,float h){FrameLayout tray=new FrameLayout(a);tray.setClipChildren(false);tray.setClipToPadding(false);tray.setBackground(AuraTheme.dock(a,a.px(22)));a.put(tray,x,y,w,h);return tray;}
    static void inside(MainActivity a,FrameLayout p,View v,float x,float y,float w,float h){FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(a.px(w),a.px(h));lp.leftMargin=a.px(x);lp.topMargin=a.px(y);p.addView(v,lp);}
    static FrameLayout app(MainActivity a,String pkg){
        FrameLayout card=new FrameLayout(a);a.focusable(card);card.setBackground(AuraTheme.states(a,a.px(a.preset().equals("immersive")?24:15),MainActivity.GOLD));card.setClipToOutline(true);card.setOutlineProvider(ViewOutlineProvider.BACKGROUND);card.setTag(pkg);card.setContentDescription(a.label(pkg));
        ImageView image=new ImageView(a);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setClipToOutline(true);image.setOutlineProvider(new ViewOutlineProvider(){public void getOutline(View v,android.graphics.Outline o){o.setRoundRect(0,0,v.getWidth(),v.getHeight(),a.px(a.preset().equals("immersive")?19:11));}});FrameLayout.LayoutParams ip=new FrameLayout.LayoutParams(-1,-1);ip.setMargins(a.px(6),a.px(6),a.px(6),a.px(6));card.addView(image,ip);
        TextView name=a.text(a.label(pkg),12,MainActivity.INK);name.setMaxLines(1);name.setEllipsize(TextUtils.TruncateAt.END);name.setGravity(Gravity.CENTER);FrameLayout.LayoutParams np=new FrameLayout.LayoutParams(-1,a.px(23),Gravity.BOTTOM);np.leftMargin=a.px(6);np.rightMargin=a.px(6);np.bottomMargin=a.px(3);card.addView(name,np);name.setVisibility(View.GONE);if(a.preset().equals("immersive")){Assets.bindIcon(a,image,pkg);name.setVisibility(View.GONE);}else Assets.bindHome(a,image,pkg,name);
        card.setOnClickListener(v->a.launch(pkg));card.setOnLongClickListener(v->{a.centerLongHandled=a.centerHeld;a.showApps(true);return true;});a.favoritesViews.add(card);return card;
    }
    static void favorites(MainActivity a,ArrayList<String> packages,FrameLayout tray,int max,float available,float height){
        float gap=12,width=(available-32-gap*(max-1))/max;
        for(int i=0;i<Math.min(max,packages.size());i++)inside(a,tray,app(a,packages.get(i)),16+i*(width+gap),16,width,height-32);
        if(a.favoritesViews.isEmpty()){TextView empty=a.button("+  Elegir aplicaciones favoritas",()->a.showApps(true));empty.setTag("choose-favorites");inside(a,tray,empty,16,16,available-32,height-32);a.favoritesViews.add(empty);}
    }
    static void classic(MainActivity a,ArrayList<String> packages){
        a.clock=a.text("",62,MainActivity.INK);a.clock.setTypeface(Typeface.create("sans-serif-light",0));a.clock.setMaxLines(2);a.put(a.clock,46,120,600,132);
        a.hero=a.text("Tu espacio, a tu manera.",17,MainActivity.MUTED);a.put(a.hero,48,248,600,30);
        FrameLayout tray=dock(a,46,306,868,108);favorites(a,packages,tray,6,868,108);actionBar(a,130,444,700);
    }
    static void modern(MainActivity a,ArrayList<String> packages){
        FrameLayout feature=dock(a,36,90,542,220);
        a.hero=a.text("Todo empieza aquí.",35,MainActivity.INK);a.hero.setTypeface(Typeface.create("sans-serif-light",0));inside(a,feature,a.hero,23,25,496,55);
        inside(a,feature,a.text("Tus aplicaciones y entradas, en un solo lugar.",15,MainActivity.MUTED),24,85,490,37);
        TextView browse=a.button("Explorar aplicaciones  ›",a::showLibrary);browse.setTag("hero-library");inside(a,feature,browse,24,151,225,41);a.headerViews.add(browse);
        FrameLayout info=dock(a,592,90,332,220);a.clock=a.text("",47,MainActivity.INK);a.clock.setMaxLines(2);a.clock.setTypeface(Typeface.create("sans-serif-light",0));inside(a,info,a.clock,23,24,286,125);inside(a,info,a.text("VELIRA TV   ·   TU TELEVISOR",11,MainActivity.MUTED),24,168,286,24);
        FrameLayout tray=dock(a,36,327,888,133);inside(a,tray,a.text("Tus aplicaciones favoritas",14,MainActivity.MUTED),18,9,800,23);favoritesModern(a,packages,tray);
        View input=utility(a,"Entradas","input",a::showInputs,0);a.put(input,760,477,164,43);
    }
    static void favoritesModern(MainActivity a,ArrayList<String> packages,FrameLayout tray){
        if(packages.isEmpty()){favorites(a,packages,tray,6,888,133);return;}
        for(int i=0;i<Math.min(6,packages.size());i++)inside(a,tray,app(a,packages.get(i)),16+i*144,38,132,80);
    }
    static void immersive(MainActivity a,ArrayList<String> packages){
        a.clock=a.text("",60,MainActivity.INK);a.clock.setTypeface(Typeface.create("sans-serif-light",0));a.clock.setMaxLines(2);a.put(a.clock,48,124,600,118);
        a.hero=a.text("Tu espacio, a tu manera.",17,MainActivity.INK);a.put(a.hero,49,244,600,30);
        FrameLayout floating=new FrameLayout(a);floating.setClipChildren(false);a.put(floating,100,311,760,126);
        int count=Math.min(6,packages.size());float width=84,gap=20,start=(760-count*width-Math.max(0,count-1)*gap)/2;
        for(int i=0;i<count;i++)inside(a,floating,app(a,packages.get(i)),start+i*(width+gap),14,width,84);
        if(count==0)favorites(a,packages,floating,6,760,126);
        a.contextName=a.text("",13,MainActivity.INK);a.contextName.setGravity(Gravity.CENTER);a.put(a.contextName,250,423,460,27);
        actionBar(a,240,472,480);
    }

    static View utility(MainActivity a,String title,String icon,Runnable action,int index){LinearLayout tile=new LinearLayout(a);tile.setGravity(Gravity.CENTER);a.focusable(tile);tile.setTag("utility-"+index);tile.setContentDescription(title);tile.setOnClickListener(v->action.run());tile.addView(new HomeGlyph(a,icon),new LinearLayout.LayoutParams(a.px(20),a.px(20)));TextView label=a.text(title,12,MainActivity.INK);label.setMaxLines(1);label.setEllipsize(TextUtils.TruncateAt.END);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-1);lp.leftMargin=a.px(8);tile.addView(label,lp);a.utilityViews.add(tile);return tile;}
    static void actionBar(MainActivity a,float x,float y,float width){FrameLayout bar=dock(a,x,y,width,43);String[] titles={"Entradas","Juegos","Aplicaciones","Ambiente"},icons={"input","game","apps","ambient"};Runnable[] actions={a::showInputs,a::showGaming,a::showLibrary,a::showAmbientSettings};float cell=(width-16)/4;for(int i=0;i<4;i++)inside(a,bar,utility(a,titles[i],icons[i],actions[i],i),8+i*cell,5,cell-4,33);}
    static void updateClock(MainActivity a){if(a.clock==null)return;Date now=new Date();String date=new SimpleDateFormat("EEEE, d 'de' MMMM",new Locale("es")).format(now);date=Character.toUpperCase(date.charAt(0))+date.substring(1);SpannableString text=new SpannableString(date+"\n"+a.timeFormat.format(now));text.setSpan(new AbsoluteSizeSpan(a.px(a.preset().equals("modern")?12:15)),0,date.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);text.setSpan(new AbsoluteSizeSpan(a.px(a.preset().equals("modern")?47:a.preset().equals("classic")?62:60)),date.length()+1,text.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);a.clock.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,a.px(15));a.clock.setText(text);}
    static void wire(MainActivity a,String preset){
        for(int i=0;i<a.headerViews.size();i++){View v=a.headerViews.get(i);v.setNextFocusLeftId(a.headerViews.get(Math.max(0,i-1)).getId());v.setNextFocusRightId(a.headerViews.get(Math.min(a.headerViews.size()-1,i+1)).getId());v.setNextFocusDownId(a.favoritesViews.get(Math.min(i,a.favoritesViews.size()-1)).getId());}
        for(int i=0;i<a.favoritesViews.size();i++){View v=a.favoritesViews.get(i);v.setNextFocusUpId(a.headerSettings.getId());v.setNextFocusLeftId(a.favoritesViews.get(Math.max(0,i-1)).getId());v.setNextFocusRightId(a.favoritesViews.get(Math.min(a.favoritesViews.size()-1,i+1)).getId());v.setNextFocusDownId(a.utilityViews.get(Math.min(i,a.utilityViews.size()-1)).getId());}
        for(int i=0;i<a.utilityViews.size();i++){View v=a.utilityViews.get(i);v.setNextFocusLeftId(a.utilityViews.get(Math.max(0,i-1)).getId());v.setNextFocusRightId(a.utilityViews.get(Math.min(a.utilityViews.size()-1,i+1)).getId());v.setNextFocusUpId(a.favoritesViews.get(Math.min(i,a.favoritesViews.size()-1)).getId());v.setNextFocusDownId(v.getId());}
    }
}
