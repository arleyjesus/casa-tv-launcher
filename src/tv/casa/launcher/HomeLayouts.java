package tv.casa.launcher;

import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

final class HomeLayouts {
    static void create(MainActivity a){
        a.scale=a.getResources().getDisplayMetrics().widthPixels/960f;
        a.root=new FrameLayout(a);a.root.setBackgroundColor(0xFF000000);a.setContentView(a.root);
        a.root.addView(MainActivity.background(a),new FrameLayout.LayoutParams(-1,-1));
        a.favoritesViews.clear();a.utilityViews.clear();
        String preset=a.preset();boolean classic=preset.equals("classic"),immersive=preset.equals("immersive");
        if(immersive&&AuraTheme.level(a)>0){ImageView glow=new ImageView(a);glow.setScaleType(ImageView.ScaleType.CENTER_CROP);glow.setAlpha(0.55f);a.root.addView(glow,new FrameLayout.LayoutParams(-1,-1));View shade=new View(a);shade.setBackgroundColor(0x880D1118);a.root.addView(shade,new FrameLayout.LayoutParams(-1,-1));a.backdrop=new FocusBackdrop(a,glow);}
        a.root.addView(new CinematicShade(a),new FrameLayout.LayoutParams(-1,-1));
        a.put(a.text("C A S A   A U R A",12,MainActivity.INK),38,26,260,24);
        a.clock=a.text("",15,MainActivity.INK);a.clock.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);a.put(a.clock,758,26,90,24);
        TextView settings=a.button("⚙",a::showSettings);a.headerSettings=settings;settings.setTag("settings");settings.setContentDescription("Centro de control");a.put(settings,868,20,48,36);
        a.hero=a.text("Tu espacio.",immersive?42:49,MainActivity.INK);a.hero.setTypeface(Typeface.create("sans-serif-light",0));a.put(a.hero,38,immersive?150:240,650,72);
        ArrayList<String> packages=a.favoritePackages();
        if(immersive)immersive(a,packages);else if(classic)classic(a,packages);else modern(a,packages);
        if(a.favoritesViews.isEmpty()){TextView empty=a.button("Elegir favoritas",()->a.showApps(true));a.put(empty,38,356,220,80);a.favoritesViews.add(empty);}
        wire(a,preset);a.restoreFocus();a.applyAppearance();
    }
    static LinearLayout app(MainActivity a,String pkg,boolean large){
        LinearLayout card=new LinearLayout(a);card.setOrientation(large?LinearLayout.VERTICAL:LinearLayout.HORIZONTAL);card.setGravity(Gravity.CENTER);card.setPadding(a.px(large?8:11),a.px(8),a.px(8),a.px(8));a.focusable(card);card.setTag(pkg);card.setContentDescription(a.label(pkg));
        ImageView icon=new ImageView(a);icon.setScaleType(ImageView.ScaleType.FIT_CENTER);icon.setImageDrawable(Assets.icon(a,pkg));card.addView(icon,new LinearLayout.LayoutParams(a.px(large?50:28),a.px(large?50:28)));
        TextView name=a.text(a.label(pkg),large?15:13,MainActivity.INK);name.setMaxLines(1);name.setEllipsize(android.text.TextUtils.TruncateAt.END);name.setGravity(large?Gravity.CENTER:Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams np=large?new LinearLayout.LayoutParams(-1,a.px(29)):new LinearLayout.LayoutParams(0,-1,1);if(large)np.topMargin=a.px(5);else np.leftMargin=a.px(9);card.addView(name,np);
        card.setOnClickListener(v->a.launch(pkg));card.setOnLongClickListener(v->{a.showApps(true);return true;});a.favoritesViews.add(card);return card;
    }
    static void modern(MainActivity a,ArrayList<String> packages){
        a.put(a.text("TUS APLICACIONES",10,MainActivity.MUTED),39,327,500,20);a.put(a.text("TU TV",10,MainActivity.MUTED),672,327,240,20);
        float width=(618-36)/4f;for(int i=0;i<Math.min(4,packages.size());i++)a.put(app(a,packages.get(i),true),38+i*(width+12),356,width,108);
        String[] titles={"Entradas","Modo juego","Ajustes","Aplicaciones"};Runnable[] actions={a::showInputs,a::showGaming,a::showSettings,a::showLibrary};
        for(int i=0;i<4;i++){TextView tile=a.button(titles[i],actions[i]);tile.setTag("utility-"+i);a.put(tile,672+(i%2)*130,356+(i/2)*59,120,49);a.utilityViews.add(tile);}
        a.put(a.text("↓  Todas las aplicaciones",12,MainActivity.MUTED),38,487,500,24);
    }
    static void classic(MainActivity a,ArrayList<String> packages){
        a.put(a.text("F A V O R I T A S",10,MainActivity.MUTED),39,344,500,20);float width=(884-70)/6f;
        for(int i=0;i<Math.min(6,packages.size());i++)a.put(app(a,packages.get(i),false),38+i*(width+14),373,width,60);
        String[] titles={"HDMI 1","HDMI 2","HDMI 3","Modo juego","Aplicaciones","Ajustes"};
        for(int i=0;i<6;i++){final int n=i;TextView tile=a.button(titles[i],()->{if(n<3)a.openHdmi(n+1);else if(n==3)a.showGaming();else if(n==4)a.showLibrary();else a.showSettings();});tile.setTag("utility-"+i);a.put(tile,38+i*150,466,134,34);a.utilityViews.add(tile);}
    }
    static void immersive(MainActivity a,ArrayList<String> packages){
        a.put(a.text("TU FILA",10,MainActivity.MUTED),39,230,500,20);
        HorizontalScrollView carousel=new HorizontalScrollView(a);carousel.setHorizontalScrollBarEnabled(false);carousel.setSmoothScrollingEnabled(a.getPreferences(0).getBoolean("animateFocus",true));carousel.setClipToPadding(false);carousel.setPadding(a.px(5),a.px(5),a.px(5),a.px(5));
        LinearLayout row=new LinearLayout(a);row.setClipChildren(false);for(String pkg:packages){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(a.px(164),a.px(120));lp.rightMargin=a.px(14);row.addView(app(a,pkg,true),lp);}carousel.addView(row);a.put(carousel,32,258,630,138);
        a.contextName=a.text("Tus aplicaciones",29,MainActivity.INK);a.contextName.setMaxLines(2);a.put(a.contextName,690,225,230,80);
        a.put(a.text("OK para abrir · Mantén OK para editar",11,MainActivity.MUTED),690,303,230,35);
        String[] titles={"Biblioteca","Centro de control","Modo juego"};Runnable[] actions={a::showLibrary,a::showSettings,a::showGaming};
        for(int i=0;i<3;i++){TextView tile=a.button(titles[i],actions[i]);tile.setTag("utility-"+i);a.put(tile,690,352+i*44,230,34);a.utilityViews.add(tile);}
        a.put(a.text("↓  Biblioteca de aplicaciones",12,MainActivity.MUTED),38,462,590,30);
    }
    static void wire(MainActivity a,String preset){
        for(int i=0;i<a.favoritesViews.size();i++){View v=a.favoritesViews.get(i);v.setNextFocusUpId(a.headerSettings.getId());v.setNextFocusLeftId(a.favoritesViews.get(Math.max(0,i-1)).getId());v.setNextFocusRightId(i==a.favoritesViews.size()-1?a.utilityViews.get(0).getId():a.favoritesViews.get(i+1).getId());v.setNextFocusDownId(preset.equals("classic")?a.utilityViews.get(Math.min(i,5)).getId():v.getId());}
        for(int i=0;i<a.utilityViews.size();i++){View v=a.utilityViews.get(i);if(preset.equals("modern")){v.setNextFocusLeftId(i%2==0?a.favoritesViews.get(a.favoritesViews.size()-1).getId():a.utilityViews.get(i-1).getId());v.setNextFocusRightId(i%2==0?a.utilityViews.get(i+1).getId():v.getId());v.setNextFocusUpId(i<2?a.headerSettings.getId():a.utilityViews.get(i-2).getId());v.setNextFocusDownId(i<2?a.utilityViews.get(i+2).getId():v.getId());}
            else if(preset.equals("classic")){v.setNextFocusLeftId(a.utilityViews.get(Math.max(0,i-1)).getId());v.setNextFocusRightId(a.utilityViews.get(Math.min(5,i+1)).getId());v.setNextFocusUpId(a.favoritesViews.get(Math.min(i,a.favoritesViews.size()-1)).getId());v.setNextFocusDownId(v.getId());}
            else{v.setNextFocusLeftId(a.favoritesViews.get(a.favoritesViews.size()-1).getId());v.setNextFocusRightId(v.getId());v.setNextFocusUpId(i==0?a.headerSettings.getId():a.utilityViews.get(i-1).getId());v.setNextFocusDownId(i==a.utilityViews.size()-1?v.getId():a.utilityViews.get(i+1).getId());}}
        a.headerSettings.setNextFocusDownId(a.favoritesViews.get(a.favoritesViews.size()-1).getId());
    }
}
