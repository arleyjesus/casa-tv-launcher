package tv.casa.launcher;

import android.app.PendingIntent;
import android.app.slice.*;
import android.content.Context;
import android.net.Uri;
import android.os.*;
import java.util.*;
import java.util.concurrent.*;

/** Uses TCL's installed Slice actions. No vendor binaries, SDK keys or shell commands. */
final class TclPictureBridge {
    static final Uri PAGE=Uri.parse("content://com.tcl.settings.slice/intelligent_picture");
    final Context context;
    static final class State {
        final Boolean enabled;final PendingIntent permission,toggle;
        State(Boolean enabled,PendingIntent permission,PendingIntent toggle){this.enabled=enabled;this.permission=permission;this.toggle=toggle;}
    }
    TclPictureBridge(Context context){this.context=context.getApplicationContext();}
    State read(){
        if(Build.VERSION.SDK_INT<28)throw new IllegalStateException("Android version unsupported");
        SliceManager manager=context.getSystemService(SliceManager.class);
        Set<SliceSpec> specs=new HashSet<>();specs.add(new SliceSpec("androidx.slice.LIST",2));specs.add(new SliceSpec("androidx.slice.BASIC",1));
        Slice page=manager.bindSlice(PAGE,specs);if(page==null)throw new IllegalStateException("TCL picture page unavailable");
        if(page.getHints().contains("permission_request")){
            ArrayList<SliceItem> actions=new ArrayList<>();collect(page,actions,false);
            if(actions.size()!=1||!PictureTogglePolicy.CREATOR.equals(actions.get(0).getAction().getCreatorPackage()))throw new IllegalStateException("Unexpected permission action");
            return new State(null,actions.get(0).getAction(),null);
        }
        for(SliceItem row:page.getItems()){
            if(!"slice".equals(row.getFormat())||!"TYPE_PREFERENCE".equals(row.getSubType()))continue;
            String key=null;int enabled=0;for(SliceItem item:row.getSlice().getItems()){
                if("text".equals(item.getFormat())&&"TAG_KEY".equals(item.getSubType()))key=String.valueOf(item.getText());
                if("int".equals(item.getFormat())&&"SUBTYPE_IS_ENABLED".equals(item.getSubType()))enabled=item.getInt();
            }
            if(!PictureTogglePolicy.KEY.equals(key))continue;
            ArrayList<SliceItem> actions=new ArrayList<>();collect(row.getSlice(),actions,true);
            PictureTogglePolicy.validate(key,enabled,actions.size(),actions.size()==1?actions.get(0).getAction().getCreatorPackage():null);
            SliceItem toggle=actions.get(0);return new State(toggle.getSlice().getHints().contains("selected"),null,toggle.getAction());
        }
        throw new IllegalStateException("Validated TCL control unavailable");
    }
    private static void collect(Slice slice,List<SliceItem> actions,boolean togglesOnly){
        for(SliceItem item:slice.getItems()){
            if("action".equals(item.getFormat())&&(!togglesOnly||"toggle".equals(item.getSubType())))actions.add(item);
            else if("slice".equals(item.getFormat()))collect(item.getSlice(),actions,togglesOnly);
        }
    }
    void set(boolean desired,boolean saveUndo)throws Exception{
        State before=read();if(!PictureTogglePolicy.shouldSend(before.enabled,desired))return;
        if(saveUndo&&!context.getSharedPreferences("TclPictureUndo",0).edit().putBoolean("previous",before.enabled).putLong("savedAt",System.currentTimeMillis()).commit())throw new IllegalStateException("Could not save original state");
        CountDownLatch delivered=new CountDownLatch(1);
        // TCL's action toggles rather than accepting a target value. Send once, never retry.
        before.toggle.send(context,0,null,(intent,fillIn,code,data,extras)->delivered.countDown(),new Handler(Looper.getMainLooper()));
        if(!delivered.await(5,TimeUnit.SECONDS))throw new IllegalStateException("Delivery not confirmed; do not retry");
        State after=read();if(after.enabled==null||after.enabled.booleanValue()!=desired)throw new IllegalStateException("Picture change not confirmed; read before another action");
    }
}
