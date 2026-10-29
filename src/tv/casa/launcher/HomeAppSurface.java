package tv.casa.launcher;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Bounded static halo and contact shadow; no bitmap, blur pass or animation clock. */
final class HomeAppSurface extends Drawable {
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float radius,unit;
    private final int level;
    private final boolean floating;
    private boolean focused;
    private RadialGradient shadow;
    HomeAppSurface(MainActivity owner,float radius,boolean floating){this.radius=radius;this.unit=owner.px(1);this.level=AuraTheme.level(owner);this.floating=floating;}
    protected void onBoundsChange(Rect bounds){shadow=new RadialGradient(0,0,1,new int[]{0x66000000,0x00000000},null,Shader.TileMode.CLAMP);}
    public void draw(Canvas canvas){
        Rect b=getBounds();float w=b.width(),h=b.height();
        if(level>0&&floating){
            int save=canvas.save();canvas.translate(b.exactCenterX(),b.bottom+8*unit);canvas.scale(w*.64f,12*unit);
            paint.setShader(shadow);canvas.drawCircle(0,0,1,paint);paint.setShader(null);canvas.restoreToCount(save);
        }
        if(level>0&&focused){
            paint.setStyle(Paint.Style.STROKE);
            for(int i=4;i>=1;i--){float spread=i*unit;paint.setStrokeWidth(2*unit);paint.setColor(Color.argb((5-i)*level*4,110,205,245));canvas.drawRoundRect(b.left-spread,b.top-spread,b.right+spread,b.bottom+spread,radius+spread,radius+spread,paint);}
        }
        paint.setStyle(Paint.Style.FILL);paint.setColor(level==0?0xFF171B22:focused?0xAA515D69:0x80292E36);
        canvas.drawRoundRect(b.left,b.top,b.right,b.bottom,radius,radius,paint);
        paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(focused?2*unit:unit);paint.setColor(focused?AuraTheme.ACCENT:0x668D99A4);
        canvas.drawRoundRect(b.left+unit,b.top+unit,b.right-unit,b.bottom-unit,radius,radius,paint);paint.setStyle(Paint.Style.FILL);
    }
    public boolean isStateful(){return true;}
    protected boolean onStateChange(int[] states){boolean next=false;for(int s:states)if(s==android.R.attr.state_focused||s==android.R.attr.state_pressed)next=true;if(next==focused)return false;focused=next;invalidateSelf();return true;}
    public void getOutline(Outline outline){outline.setRoundRect(getBounds(),radius);outline.setAlpha(.5f);}
    public void setAlpha(int alpha){}
    public void setColorFilter(ColorFilter filter){}
    public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
