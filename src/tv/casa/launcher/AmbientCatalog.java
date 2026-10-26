package tv.casa.launcher;

import org.json.*;
import java.io.*;
import java.util.*;

/** A content catalog cannot change app preferences, run actions or grant permissions. */
final class AmbientCatalog {
    static final class Variant {String url,sha;int bytes,width,height;}
    static final class Item {String id,title,type,author,license,source;final ArrayList<Variant> variants=new ArrayList<>();boolean image(){return type.equals("image");}Variant choose(int maxWidth){Variant best=null;if(image()){for(Variant v:variants)if(v.width>=maxWidth&&(best==null||v.width<best.width))best=v;if(best!=null)return best;for(Variant v:variants)if(best==null||v.width>best.width)best=v;return best;}for(Variant v:variants)if(v.width<=maxWidth&&(best==null||v.width>best.width))best=v;if(best==null)for(Variant v:variants)if(best==null||v.width<best.width)best=v;return best;}}
    static final class Collection {String id,name;final ArrayList<Item> items=new ArrayList<>();}
    final ArrayList<Collection> collections=new ArrayList<>();
    static AmbientCatalog parse(String text)throws Exception{
        if(!OnlinePolicy.jsonDepth(text))throw new IOException("Catálogo demasiado complejo");JSONObject doc=new JSONObject(text);if(!(doc.get("schema") instanceof Integer)||doc.getInt("schema")!=2)throw new IOException("Versión de catálogo no compatible");JSONArray groups=doc.getJSONArray("collections");if(groups.length()<1||groups.length()>OnlinePolicy.MAX_COLLECTIONS)throw new IOException("Número de colecciones no válido");
        AmbientCatalog result=new AmbientCatalog();HashSet<String> ids=new HashSet<>(),groupsSeen=new HashSet<>();int count=0;
        for(int i=0;i<groups.length();i++){JSONObject g=groups.getJSONObject(i);Collection c=new Collection();c.id=g.getString("id");c.name=OnlinePolicy.text(g.getString("name"),64);if(!OnlinePolicy.id(c.id)||!groupsSeen.add(c.id)||c.name.isEmpty())throw new IOException("Colección no válida");JSONArray list=g.getJSONArray("items");if(list.length()<1)throw new IOException("Colección vacía");
            for(int j=0;j<list.length();j++){if(++count>OnlinePolicy.MAX_ITEMS)throw new IOException("Demasiados recursos");JSONObject o=list.getJSONObject(j);Item m=new Item();m.id=o.getString("id");m.title=OnlinePolicy.text(o.getString("title"),80);m.type=o.getString("type");m.author=OnlinePolicy.text(o.getString("author"),120);m.license=OnlinePolicy.text(o.getString("license"),120);m.source=o.getString("source");if(!OnlinePolicy.id(m.id)||!ids.add(m.id)||m.title.isEmpty()||m.author.isEmpty()||m.license.isEmpty()||!OnlinePolicy.https(m.source)||(!m.type.equals("image")&&!m.type.equals("mp4")))throw new IOException("Recurso no válido");JSONArray variants=o.getJSONArray("variants");if(variants.length()<1||variants.length()>3)throw new IOException("Variantes no válidas");
                for(int k=0;k<variants.length();k++){JSONObject v=variants.getJSONObject(k);Variant x=new Variant();x.url=v.getString("url");if(!(v.get("width") instanceof Integer)||!(v.get("height") instanceof Integer))throw new IOException("Dimensiones no válidas");x.width=v.getInt("width");x.height=v.getInt("height");if(!OnlinePolicy.https(x.url)||!OnlinePolicy.dimensions(x.width,x.height))throw new IOException("URL o dimensiones no válidas");if(m.image()){x.sha=v.getString("sha256");Object size=v.get("bytes");x.bytes=v.getInt("bytes");if(!(size instanceof Integer)||!OnlinePolicy.hash(x.sha)||x.bytes<1||x.bytes>OnlinePolicy.MAX_IMAGE)throw new IOException("Tamaño o hash no válido");}m.variants.add(x);}
                c.items.add(m);
            }result.collections.add(c);
        }return result;
    }
    Collection collection(String id){for(Collection c:collections)if(c.id.equals(id))return c;return collections.get(0);}
    Item find(String id){for(Collection c:collections)for(Item m:c.items)if(m.id.equals(id))return m;return null;}
}
