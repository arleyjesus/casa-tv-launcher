package tv.casa.launcher;
import android.content.*;
/** Shell-only export endpoint. Ordinary apps do not hold android.permission.DUMP. */
public class BackupReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context,Intent intent){
        if(!"tv.casa.launcher.EXPORT_CONFIGURATION".equals(intent.getAction()))return;
        try{setResultCode(1);setResultData(Configuration.export(context).getAbsolutePath());}
        catch(Exception error){setResultCode(0);setResultData("No se pudo exportar");android.util.Log.w("Casa","Backup failed",error);}
    }
}
