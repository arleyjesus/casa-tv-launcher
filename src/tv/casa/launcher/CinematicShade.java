package tv.casa.launcher;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/** Lower-third fade, recreated only on resize. Does not obscure or intercept controls. */
final class CinematicShade extends View {
    private final Paint paint=new Paint(Paint.DITHER_FLAG);private float start;
    CinematicShade(Context c){super(c);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
    protected void onSizeChanged(int w,int h,int oldw,int oldh){start=h*.68f;paint.setShader(new LinearGradient(0,start,0,h,new int[]{0x00000000,0x18000000,0x88000000},new float[]{0,.48f,1},Shader.TileMode.CLAMP));}
    protected void onDraw(Canvas c){super.onDraw(c);c.drawRect(0,start,getWidth(),getHeight(),paint);}
}
