package tv.casa.launcher;

import android.app.Dialog;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

/** Native control surface: static translucent fills, no blur or additional artwork. */
final class AuraControlPanel {
    static Dialog show(MainActivity a, Runnable[] actions) {
        String[] titles={"Apariencia","Favoritas","Ordenar","Ajustes del TV","Conexiones","Ambiente","Copias","Inicio","Acerca de Velira"};
        String[] subtitles={"Título, reloj y selección","Tus seis esenciales","Cada app en su lugar","Imagen, sonido y más","Wi-Fi y Bluetooth","Un paisaje en reposo","Guardar y recuperar","Estado y recuperación","Diseñado para tu espacio"};
        if(actions.length!=titles.length)throw new IllegalArgumentException("Control actions mismatch");
        a.closePanel();
        Dialog d=new Dialog(a);
        LinearLayout surface=new LinearLayout(a);surface.setOrientation(1);surface.setPadding(a.px(26),a.px(20),a.px(26),a.px(18));
        GradientDrawable backdrop=AuraTheme.panel(a,a.px(25));surface.setBackground(backdrop);
        LinearLayout header=new LinearLayout(a);header.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout words=new LinearLayout(a);words.setOrientation(1);
        TextView brand=a.text("V E L I R A   T V",10,MainActivity.GOLD);words.addView(brand,new LinearLayout.LayoutParams(-1,a.px(20)));
        TextView title=a.text("Centro de control",29,MainActivity.INK);title.setTypeface(Typeface.create("sans-serif-medium",0));words.addView(title,new LinearLayout.LayoutParams(-1,a.px(43)));
        header.addView(words,new LinearLayout.LayoutParams(0,a.px(66),1));
        TextView close=a.button("×",a::closePanel);close.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,a.px(25));close.setContentDescription("Cerrar centro de control");header.addView(close,new LinearLayout.LayoutParams(a.px(42),a.px(42)));
        surface.addView(header,new LinearLayout.LayoutParams(-1,a.px(70)));
        View[] cards=new View[9];
        for(int row=0;row<3;row++){
            LinearLayout line=new LinearLayout(a);line.setClipChildren(false);line.setClipToPadding(false);
            LinearLayout.LayoutParams rowParams=new LinearLayout.LayoutParams(-1,a.px(91));rowParams.topMargin=a.px(10);surface.addView(line,rowParams);
            for(int col=0;col<3;col++){
                final int index=row*3+col;
                LinearLayout card=new LinearLayout(a);card.setOrientation(1);card.setPadding(a.px(16),a.px(10),a.px(12),a.px(8));a.focusable(card);
                card.setContentDescription(titles[index]+". "+subtitles[index]);
                LinearLayout cardTop=new LinearLayout(a);cardTop.setGravity(Gravity.CENTER_VERTICAL);
                cardTop.addView(new Icon(a,index),new LinearLayout.LayoutParams(a.px(22),a.px(22)));
                TextView name=a.text(titles[index],16,MainActivity.INK);name.setTypeface(Typeface.create("sans-serif-medium",0));LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(0,a.px(30),1);np.leftMargin=a.px(10);cardTop.addView(name,np);card.addView(cardTop,new LinearLayout.LayoutParams(-1,a.px(34)));
                TextView caption=a.text(subtitles[index],11,MainActivity.MUTED);caption.setMaxLines(1);caption.setEllipsize(android.text.TextUtils.TruncateAt.END);card.addView(caption,new LinearLayout.LayoutParams(-1,a.px(27)));
                card.setOnClickListener(v->{a.closePanel();actions[index].run();});cards[index]=card;
                LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(0,-1,1);if(col>0)cp.leftMargin=a.px(12);line.addView(card,cp);
            }
        }
        for(int i=0;i<cards.length;i++){
            int row=i/3,col=i%3;View card=cards[i];
            card.setNextFocusLeftId(cards[row*3+Math.max(0,col-1)].getId());card.setNextFocusRightId(cards[row*3+Math.min(2,col+1)].getId());
            card.setNextFocusUpId(row==0?close.getId():cards[i-3].getId());card.setNextFocusDownId(row==2?card.getId():cards[i+3].getId());
        }
        close.setNextFocusDownId(cards[2].getId());close.setNextFocusUpId(close.getId());
        TextView hint=a.text("Flechas para explorar   ·   OK para abrir   ·   Atrás para volver",11,MainActivity.MUTED);LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,a.px(24));hp.topMargin=a.px(14);surface.addView(hint,hp);
        d.setContentView(surface);d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);d.getWindow().setGravity(Gravity.CENTER);d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        Window window=d.getWindow();
        WindowManager.LayoutParams attributes=window.getAttributes();
        attributes.width=a.px(828);attributes.height=a.px(452);attributes.dimAmount=0.48f;
        window.setAttributes(attributes);
        window.getDecorView().setSystemUiVisibility(5894);
        d.setOnDismissListener(v->a.panelDismissed(d));d.show();a.activeDialog=d;cards[0].requestFocus();return d;
    }

    private static final class Icon extends View {
        final int kind;final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        Icon(MainActivity a,int kind){super(a);this.kind=kind;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
        protected void onDraw(Canvas canvas){
            super.onDraw(canvas);canvas.save();canvas.scale(getWidth()/24f,getHeight()/24f);paint.setColor(MainActivity.GOLD);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.5f);paint.setStrokeCap(Paint.Cap.ROUND);
            switch(kind){
            case 0:for(int y=6;y<=18;y+=6){canvas.drawLine(3,y,21,y,paint);canvas.drawCircle(y==12?15:8,y,2,paint);}break;
            case 1:for(int y=3;y<=13;y+=10)for(int x=3;x<=13;x+=10)canvas.drawRoundRect(x,y,x+7,y+7,2,2,paint);break;
            case 2:canvas.drawLine(3,8,20,8,paint);canvas.drawLine(3,16,20,16,paint);canvas.drawLine(16,4,20,8,paint);canvas.drawLine(16,12,20,8,paint);canvas.drawLine(7,12,3,16,paint);canvas.drawLine(7,20,3,16,paint);break;
            case 3:canvas.drawRoundRect(2,4,22,17,3,3,paint);canvas.drawLine(8,21,16,21,paint);canvas.drawLine(12,17,12,21,paint);break;
            case 4:canvas.drawArc(2,4,22,23,220,100,false,paint);canvas.drawArc(6,9,18,21,220,100,false,paint);canvas.drawCircle(12,19,1,paint);break;
            case 5:canvas.drawRoundRect(2,3,22,21,3,3,paint);canvas.drawCircle(17,8,2,paint);canvas.drawLine(4,18,10,11,paint);canvas.drawLine(10,11,19,20,paint);break;
            case 6:canvas.drawRoundRect(3,6,21,21,3,3,paint);canvas.drawLine(12,15,12,2,paint);canvas.drawLine(8,6,12,2,paint);canvas.drawLine(16,6,12,2,paint);break;
            case 7:Path home=new Path();home.moveTo(3,11);home.lineTo(12,3);home.lineTo(21,11);home.lineTo(21,21);home.lineTo(15,21);home.lineTo(15,14);home.lineTo(9,14);home.lineTo(9,21);home.lineTo(3,21);home.close();canvas.drawPath(home,paint);break;
            default:canvas.drawCircle(12,12,10,paint);canvas.drawLine(12,11,12,18,paint);canvas.drawCircle(12,7,0.8f,paint);
            }canvas.restore();
        }
    }
}
