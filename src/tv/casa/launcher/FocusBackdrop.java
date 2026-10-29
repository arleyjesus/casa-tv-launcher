package tv.casa.launcher;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.widget.ImageView;

/** Tiny selection-driven blur, computed once off-thread. No live wallpaper blur. */
final class FocusBackdrop {
    private final MainActivity owner;private final ImageView view;
    private final LruCache<String,Bitmap> cache=new LruCache<>(4);
    private int generation;private Runnable pending;
    FocusBackdrop(MainActivity a,ImageView view){owner=a;this.view=view;}
    void select(String pkg){
        if(owner.contextName!=null)owner.contextName.setText(owner.label(pkg));
        if(!owner.resumed)return;
        if(pending!=null)owner.handler.removeCallbacks(pending);final int request=++generation;
        Bitmap cached=cache.get(pkg);if(cached!=null){view.setImageBitmap(cached);return;}
        pending=()->owner.loader.execute(()->{Bitmap result=soften(Assets.icon(owner,pkg));owner.handler.post(()->{if(!owner.alive||!owner.resumed||request!=generation||owner.backdrop!=this)return;if(result!=null){cache.put(pkg,result);view.setImageBitmap(result);}});});owner.handler.postDelayed(pending,90);
    }
    void pause(){generation++;if(pending!=null)owner.handler.removeCallbacks(pending);pending=null;}
    void stop(){generation++;if(pending!=null)owner.handler.removeCallbacks(pending);view.setImageDrawable(null);cache.evictAll();}
    static Bitmap soften(Drawable icon){
        if(icon==null)return null;int w=48,h=27;Bitmap small=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(small);icon.setBounds(0,-10,w,w-10);icon.draw(canvas);
        int[] pixels=new int[w*h],out=new int[w*h];small.getPixels(pixels,0,w,0,0,w,h);
        for(int pass=0;pass<3;pass++){for(int y=0;y<h;y++)for(int x=0;x<w;x++){int rr=0,gg=0,bb=0,n=0;for(int dy=-2;dy<=2;dy++)for(int dx=-2;dx<=2;dx++){int c=pixels[Math.max(0,Math.min(h-1,y+dy))*w+Math.max(0,Math.min(w-1,x+dx))];rr+=(c>>16)&255;gg+=(c>>8)&255;bb+=c&255;n++;}out[y*w+x]=Color.rgb(rr/n,gg/n,bb/n);}int[] swap=pixels;pixels=out;out=swap;}
        small.setPixels(pixels,0,w,0,0,w,h);return small;
    }
}
