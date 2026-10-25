package tv.casa.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

final class Configuration {
    static SharedPreferences prefs(Context context){return context.getSharedPreferences("MainActivity",0);}
    static File directory(Context context)throws IOException{
        File base=context.getExternalFilesDir(null);
        if(base==null)throw new IOException("Almacenamiento no disponible");
        File folder=new File(base,"backups");
        if(!folder.isDirectory()&&!folder.mkdirs())throw new IOException("No se pudo crear la copia");
        return folder;
    }
    static File export(Context context)throws Exception{
        SharedPreferences p=prefs(context);JSONObject data=new JSONObject();data.put("schema",1);
        String raw=p.getString("favorites",String.join(",",MainActivity.DEFAULT_FAVORITES));
        data.put("favorites",new JSONArray(Arrays.asList(raw.split(","))));
        for(String option:new String[]{"showTitle","showClock","animateFocus"})data.put(option,p.getBoolean(option,true));
        for(String key:DisplayOptions.STRING_KEYS)data.put(key,p.getString(key,DisplayOptions.defaultString(key)));
        for(String key:DisplayOptions.INT_KEYS)data.put(key,p.getInt(key,DisplayOptions.defaultInt(key)));
        for(String key:DisplayOptions.BOOL_KEYS)data.put(key,p.getBoolean(key,false));
        File result=new File(directory(context),"Casa-"+System.currentTimeMillis()+".json");
        try(FileOutputStream stream=new FileOutputStream(result)){stream.write(data.toString(2).getBytes(StandardCharsets.UTF_8));}
        return result;
    }
    static File[] copies(Context context)throws IOException{
        File[] files=directory(context).listFiles((folder,name)->name.startsWith("Casa-")&&name.endsWith(".json"));
        if(files==null)return new File[0];
        Arrays.sort(files,(a,b)->Long.compare(b.lastModified(),a.lastModified()));return files;
    }
    static JSONObject read(File file)throws Exception{
        if(file.length()>16384)throw new IOException("Copia demasiado grande");
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        try(InputStream input=new FileInputStream(file)){byte[] block=new byte[1024];int length;while((length=input.read(block))!=-1){bytes.write(block,0,length);if(bytes.size()>16384)throw new IOException("Copia demasiado grande");}}
        JSONObject data=new JSONObject(new String(bytes.toByteArray(),StandardCharsets.UTF_8));
        if(data.getInt("schema")!=1)throw new IOException("Formato no compatible");
        JSONArray favorites=data.getJSONArray("favorites");if(favorites.length()>6)throw new IOException("Demasiadas favoritas");
        HashSet<String> seen=new HashSet<>();for(int i=0;i<favorites.length();i++){String pkg=favorites.getString(i);if(!pkg.matches("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z0-9_]+)+")||!seen.add(pkg))throw new IOException("Favoritas no válidas");}
        for(String option:new String[]{"showTitle","showClock","animateFocus"})if(!(data.get(option) instanceof Boolean))throw new IOException("Opción no válida");
        for(String[] keys:new String[][]{DisplayOptions.STRING_KEYS,DisplayOptions.INT_KEYS,DisplayOptions.BOOL_KEYS})for(String key:keys)if(data.has(key)&&!DisplayOptions.valid(key,data.get(key)))throw new IOException("Opción de diseño o ambiente no válida");
        return data;
    }
    static void restore(Context context,File source)throws Exception{
        JSONObject data=read(source);export(context);
        ArrayList<String> favorites=new ArrayList<>();JSONArray list=data.getJSONArray("favorites");for(int i=0;i<list.length();i++)favorites.add(list.getString(i));
        SharedPreferences.Editor editor=prefs(context).edit().putString("favorites",String.join(",",favorites));
        for(String option:new String[]{"showTitle","showClock","animateFocus"})editor.putBoolean(option,data.getBoolean(option));
        for(String key:DisplayOptions.STRING_KEYS)if(data.has(key))editor.putString(key,data.getString(key));
        for(String key:DisplayOptions.INT_KEYS)if(data.has(key))editor.putInt(key,data.getInt(key));
        for(String key:DisplayOptions.BOOL_KEYS)if(data.has(key))editor.putBoolean(key,data.getBoolean(key));
        if(!editor.commit())throw new IOException("No se pudo guardar");
    }
}
