package tv.casa.launcher;
import android.content.Context;
import android.graphics.*;
import android.view.View;
/** Original line icons; one static path per control, no font-dependent glyphs. */
final class HomeGlyph extends View {
    private final String kind;private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    HomeGlyph(Context c,String kind){super(c);this.kind=kind;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
    protected void onDraw(Canvas c){super.onDraw(c);c.save();float edge=Math.min(getWidth(),getHeight())*.56f;c.translate((getWidth()-edge)/2,(getHeight()-edge)/2);c.scale(edge/24,edge/24);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.7f);p.setColor(AuraTheme.INK);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);
        if(kind.equals("input")){Path path=new Path();path.moveTo(2,6);path.lineTo(22,6);path.lineTo(22,13);path.lineTo(17,18);path.lineTo(7,18);path.lineTo(2,13);path.close();c.drawPath(path,p);c.drawLine(7,10,17,10,p);}
        else if(kind.equals("apps")){for(int y=3;y<=13;y+=10)for(int x=3;x<=13;x+=10)c.drawRoundRect(x,y,x+7,y+7,1.5f,1.5f,p);}
        else if(kind.equals("ambient")){Path path=new Path();path.moveTo(4,21);path.cubicTo(1,9,10,3,22,2);path.cubicTo(21,16,14,22,4,21);path.moveTo(3,22);path.lineTo(17,8);c.drawPath(path,p);}
        else if(kind.equals("game")){Path path=new Path();path.moveTo(7,5);path.cubicTo(2,5,1,13,2,19);path.cubicTo(3,23,8,15,9,15);path.lineTo(15,15);path.cubicTo(16,15,21,23,22,19);path.cubicTo(23,13,22,5,17,5);path.close();c.drawPath(path,p);c.drawLine(5,10,10,10,p);c.drawLine(7.5f,7.5f,7.5f,12.5f,p);c.drawCircle(16,9,1,p);c.drawCircle(19,12,1,p);}
        else if(kind.equals("web")){c.drawCircle(12,12,10,p);c.drawOval(7,2,17,22,p);c.drawLine(2,12,22,12,p);c.drawLine(4,7,20,7,p);c.drawLine(4,17,20,17,p);}
        else{c.drawCircle(12,12,6,p);c.drawCircle(12,12,2,p);for(int i=0;i<8;i++){c.save();c.rotate(i*45,12,12);c.drawLine(12,2,12,5,p);c.restore();}}
        c.restore();
    }
}
