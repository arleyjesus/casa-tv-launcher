package tv.casa.launcher;

import android.content.*;
import org.json.*;
import java.util.*;

/** Local, bounded lists. No browsing data is uploaded or included in launcher backups. */
final class GameStore {
    final SharedPreferences prefs;
    GameStore(Context c){prefs=c.getSharedPreferences("Games",0);}
    static final class Page {final String title,url;Page(String t,String u){title=t;url=u;}}
    ArrayList<Page> pages(String key){ArrayList<Page> out=new ArrayList<>();try{JSONArray a=new JSONArray(prefs.getString(key,"[]"));for(int i=0;i<Math.min(GamePolicy.LIMIT,a.length());i++){JSONObject o=a.getJSONObject(i);String u=o.getString("url");if(GamePolicy.safeUrl(u))out.add(new Page(GamePolicy.title(o.getString("title")),u));}}catch(Exception ignored){}return out;}
    void remember(String key,String title,String url){if(!GamePolicy.safeUrl(url))return;ArrayList<Page> list=pages(key);list.removeIf(p->p.url.equals(url));list.add(0,new Page(GamePolicy.title(title),url));JSONArray a=new JSONArray();try{for(int i=0;i<Math.min(GamePolicy.LIMIT,list.size());i++){Page p=list.get(i);a.put(new JSONObject().put("title",p.title).put("url",p.url));}prefs.edit().putString(key,a.toString()).apply();}catch(Exception ignored){}}
    String alias(int port){return prefs.getString("hdmi-name-"+port,port==3?"HDMI 3 · ARC/eARC":"HDMI "+port);}
    void alias(int port,String value){String s=value==null?"":value.replaceAll("[\\p{Cntrl}]"," ").trim();if(s.isEmpty())prefs.edit().remove("hdmi-name-"+port).apply();else prefs.edit().putString("hdmi-name-"+port,s.substring(0,Math.min(40,s.length()))).apply();}
    long last(String key){return prefs.getLong(key,0);}
    void used(String key){prefs.edit().putLong(key,System.currentTimeMillis()).apply();}
    void clear(){prefs.edit().remove("recent").remove("favorites").apply();}
}
