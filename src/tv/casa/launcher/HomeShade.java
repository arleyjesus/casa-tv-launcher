package tv.casa.launcher;
import android.content.Context;
import android.graphics.*;
import android.view.View;
/** Readability gradients belong to the UI, never to the wallpaper. */
final class HomeShade extends View {
    private final Paint left=new Paint(Paint.DITHER_FLAG),bottom=new Paint(Paint.DITHER_FLAG),top=new Paint(Paint.DITHER_FLAG);
    HomeShade(Context c){super(c);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
    protected void onSizeChanged(int w,int h,int ow,int oh){left.setShader(new LinearGradient(0,0,w*.8f,0,0x66171B22,0x00020C20,Shader.TileMode.CLAMP));bottom.setShader(new LinearGradient(0,h*.52f,0,h,0x00020C20,0x99171B22,Shader.TileMode.CLAMP));top.setShader(new LinearGradient(0,0,0,h*.23f,0x55171B22,0x00020C20,Shader.TileMode.CLAMP));}
    protected void onDraw(Canvas c){c.drawRect(0,0,getWidth(),getHeight(),left);c.drawRect(0,0,getWidth(),getHeight(),bottom);c.drawRect(0,0,getWidth(),getHeight(),top);}
}
