package tv.casa.launcher;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.util.*;

/** Created on first use. GridView recycles cells; discovery uses the existing worker. */
final class AppLibraryView extends LinearLayout {
    private final MainActivity owner;
    private final GridView grid;
    private final TextView count;
    private final ArrayList<String> packages=new ArrayList<>();
    private final LibraryAdapter adapter=new LibraryAdapter();
    private int generation;

    AppLibraryView(MainActivity a){
        super(a);owner=a;setOrientation(VERTICAL);setPadding(a.px(24),a.px(15),a.px(24),a.px(12));
        GradientDrawable surface=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0xFC302F2D,0xFE171A1D});surface.setCornerRadius(a.px(23));surface.setStroke(a.px(0.7f),0x99C5C0B8);setBackground(surface);
        LinearLayout header=new LinearLayout(a);header.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=a.text("Todas las aplicaciones",24,MainActivity.INK);header.addView(title,new LayoutParams(0,a.px(40),1));
        TextView close=a.button("×",a::closeLibrary);close.setContentDescription("Volver a Inicio");header.addView(close,new LayoutParams(a.px(40),a.px(36)));addView(header,new LayoutParams(-1,a.px(42)));
        count=a.text("Arriba para volver a Inicio",11,MainActivity.MUTED);addView(count,new LayoutParams(-1,a.px(25)));
        grid=new GridView(a);grid.setNumColumns(6);grid.setHorizontalSpacing(a.px(12));grid.setVerticalSpacing(a.px(10));grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);grid.setClipToPadding(false);grid.setPadding(a.px(3),a.px(5),a.px(3),a.px(5));
        GradientDrawable outline=new GradientDrawable();outline.setColor(Color.TRANSPARENT);outline.setCornerRadius(a.px(15));outline.setStroke(a.px(2),0xFFFFF3E6);grid.setSelector(outline);grid.setDrawSelectorOnTop(true);grid.setAdapter(adapter);
        grid.setOnItemClickListener((parent,view,position,id)->{String pkg=packages.get(position);a.closeLibrary();a.launch(pkg);});
        grid.setOnItemLongClickListener((parent,view,position,id)->{a.closeLibrary();a.showApps(true);return true;});
        grid.setOnKeyListener((view,key,event)->{if(event.getAction()==KeyEvent.ACTION_DOWN&&key==KeyEvent.KEYCODE_DPAD_UP&&grid.getSelectedItemPosition()<6){a.closeLibrary();return true;}return false;});
        addView(grid,new LayoutParams(-1,0,1));setVisibility(INVISIBLE);
    }

    void open(){
        setVisibility(VISIBLE);cancelAnimation();setAlpha(1);setTranslationY(0);
        if(owner.appCache!=null)display(owner.appCache);else reload();
        grid.requestFocus();if(grid.getSelectedItemPosition()<0&&!packages.isEmpty())grid.setSelection(0);
    }
    void hide(){generation++;cancelAnimation();setVisibility(INVISIBLE);grid.clearFocus();}
    void cancelAnimation(){animate().cancel();}
    void invalidatePackages(){generation++;if(getVisibility()==VISIBLE)reload();}
    private void reload(){
        final int request=++generation;count.setText("Cargando aplicaciones…");
        owner.loader.execute(()->{
            ArrayList<String> found=owner.apps();
            for(int i=0;i<Math.min(18,found.size());i++)Assets.icon(owner,found.get(i));
            owner.handler.post(()->{if(!owner.alive||request!=generation)return;owner.appCache=found;display(found);});
        });
    }
    private void display(List<String> found){if(!packages.equals(found)){packages.clear();packages.addAll(found);adapter.notifyDataSetChanged();}String summary=packages.size()+" aplicaciones   ·   Arriba para volver   ·   Mantén OK para editar favoritas";if(!summary.contentEquals(count.getText()))count.setText(summary);if(getVisibility()==VISIBLE)grid.requestFocus();}

    private final class LibraryAdapter extends BaseAdapter {
        public int getCount(){return packages.size();}
        public Object getItem(int position){return packages.get(position);}
        public long getItemId(int position){return position;}
        public View getView(int position,View recycled,android.view.ViewGroup parent){
            LinearLayout cell;
            if(recycled instanceof LinearLayout)cell=(LinearLayout)recycled;
            else{
                cell=new LinearLayout(owner);cell.setOrientation(VERTICAL);cell.setGravity(Gravity.CENTER);cell.setPadding(owner.px(7),owner.px(8),owner.px(7),owner.px(7));cell.setBackground(owner.surface(false));cell.setLayoutParams(new AbsListView.LayoutParams(-1,owner.px(88)));
                ImageView icon=new ImageView(owner);icon.setScaleType(ImageView.ScaleType.FIT_CENTER);cell.addView(icon,new LayoutParams(owner.px(46),owner.px(46)));
                TextView name=owner.text("",12,MainActivity.INK);name.setGravity(Gravity.CENTER);name.setMaxLines(1);name.setEllipsize(android.text.TextUtils.TruncateAt.END);LayoutParams np=new LayoutParams(-1,owner.px(23));np.topMargin=owner.px(3);cell.addView(name,np);
            }
            String pkg=packages.get(position);((ImageView)cell.getChildAt(0)).setImageDrawable(Assets.icon(owner,pkg));String name=owner.label(pkg);((TextView)cell.getChildAt(1)).setText(name);cell.setContentDescription(name);return cell;
        }
    }
}
