package tv.casa.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Explicit download only; no jobs, service, polling or network calls during playback. */
final class RemoteGallery {
    private static final Object DOWNLOAD=new Object(),STORE=new Object();
    private final File root;
    private volatile HttpURLConnection connection;
    RemoteGallery(Context context){root=new File(context.getFilesDir(),"ambient-gallery");}
    File folder(String name){return new File(root,name);}
    void cancel(){HttpURLConnection active=connection;if(active!=null)active.disconnect();}
    private void fetch(String url,OutputStream output,int limit)throws Exception{
        GalleryPolicy.checkInterrupted();HttpURLConnection active=(HttpURLConnection)new URL(url).openConnection();connection=active;
        try{active.setConnectTimeout(10000);active.setReadTimeout(10000);active.setInstanceFollowRedirects(false);active.setUseCaches(false);active.setRequestProperty("Accept-Encoding","identity");active.setRequestProperty("User-Agent","CasaAuraTV/0.7");
            if(active.getResponseCode()!=200)throw new IOException("No se pudo descargar desde GitHub");
            if(active.getContentLengthLong()>limit)throw new IOException("Archivo demasiado grande");
            try(InputStream input=active.getInputStream()){GalleryPolicy.copy(input,output,limit);}
        }finally{active.disconnect();connection=null;}
    }
    static JSONArray entries(JSONObject catalog)throws Exception{
        if(!(catalog.get("schema") instanceof Integer)||catalog.getInt("schema")!=1)throw new IOException("Catálogo no compatible");
        JSONArray images=catalog.getJSONArray("images");if(images.length()<1||images.length()>GalleryPolicy.MAX_IMAGES)throw new IOException("Número de imágenes no permitido");
        HashSet<String> names=new HashSet<>();
        for(int i=0;i<images.length();i++){JSONObject image=images.getJSONObject(i);String name=image.getString("file");Object bytes=image.get("bytes");
            if(!GalleryPolicy.filename(name)||!names.add(name)||!GalleryPolicy.imageUrl(image.getString("url"))||!image.getString("sha256").matches("[a-f0-9]{64}")||!(bytes instanceof Integer)||image.getInt("bytes")<1||image.getInt("bytes")>GalleryPolicy.MAX_IMAGE)throw new IOException("Imagen de catálogo no válida");
        }return images;
    }
    int download()throws Exception{synchronized(DOWNLOAD){GalleryPolicy.checkInterrupted();return install();}}
    private int install()throws Exception{
        if(!root.isDirectory()&&!root.mkdirs())throw new IOException("Almacenamiento no disponible");
        synchronized(STORE){recover();}File stage=folder("pending");erase(stage);if(!stage.mkdir())throw new IOException("No se pudo preparar la descarga");
        try{
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();fetch(GalleryPolicy.CATALOG,bytes,GalleryPolicy.MAX_CATALOG);
            JSONObject catalog=new JSONObject(new String(bytes.toByteArray(),StandardCharsets.UTF_8));JSONArray images=entries(catalog);
            for(int i=0;i<images.length();i++){GalleryPolicy.checkInterrupted();JSONObject image=images.getJSONObject(i);File file=new File(stage,image.getString("file"));
                try(OutputStream output=new FileOutputStream(file)){fetch(image.getString("url"),output,image.getInt("bytes"));}
                if(file.length()!=image.getInt("bytes")||!GalleryPolicy.sha256(file).equals(image.getString("sha256")))throw new IOException("La imagen no coincide con el catálogo");
                BitmapFactory.Options bounds=new BitmapFactory.Options();bounds.inJustDecodeBounds=true;BitmapFactory.decodeFile(file.getPath(),bounds);
                if(bounds.outWidth<1||bounds.outHeight<1||(long)bounds.outWidth*bounds.outHeight>GalleryPolicy.MAX_PIXELS||!"image/png".equals(bounds.outMimeType))throw new IOException("Formato o dimensiones no compatibles");
                bounds.inJustDecodeBounds=false;bounds.inSampleSize=8;Bitmap probe=BitmapFactory.decodeFile(file.getPath(),bounds);if(probe==null)throw new IOException("Imagen dañada");probe.recycle();
            }
            try(OutputStream output=new FileOutputStream(new File(stage,"catalog.json"))){output.write(bytes.toByteArray());}
            GalleryPolicy.checkInterrupted();synchronized(STORE){File current=folder("current"),previous=folder("previous");erase(previous);
            if(current.exists()&&!current.renameTo(previous))throw new IOException("No se pudo conservar la galería anterior");
            if(!stage.renameTo(current)){if(previous.exists())previous.renameTo(current);throw new IOException("No se pudo activar la galería");}
            erase(previous);return images.length();}
        }finally{erase(stage);}
    }
    private void recover()throws IOException{File current=folder("current"),previous=folder("previous");if(!current.exists()&&previous.exists()&&!previous.renameTo(current))throw new IOException("No se pudo recuperar la galería");if(current.exists()&&previous.exists())erase(previous);}
    File[] local(){synchronized(STORE){
        try{recover();File current=folder("current"),catalogFile=new File(current,"catalog.json");if(catalogFile.length()<1||catalogFile.length()>GalleryPolicy.MAX_CATALOG)return new File[0];
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(InputStream input=new FileInputStream(catalogFile)){GalleryPolicy.copy(input,bytes,GalleryPolicy.MAX_CATALOG);}JSONArray images=entries(new JSONObject(new String(bytes.toByteArray(),StandardCharsets.UTF_8)));
            File[] result=new File[images.length()];for(int i=0;i<result.length;i++){JSONObject image=images.getJSONObject(i);result[i]=new File(current,image.getString("file"));if(result[i].length()!=image.getInt("bytes"))return new File[0];}return result;
        }catch(Exception e){return new File[0];}
    }}
    void clear()throws IOException{synchronized(DOWNLOAD){synchronized(STORE){erase(folder("pending"));erase(folder("current"));erase(folder("previous"));}}}
    private void erase(File file)throws IOException{
        // Only private, fixed gallery folders and validated child names can reach here.
        if(!file.getCanonicalPath().startsWith(root.getCanonicalPath()+File.separator))throw new IOException("Ruta no válida");
        if(file.isDirectory()){File[] children=file.listFiles();if(children==null)throw new IOException("No se pudo leer la galería");for(File child:children)erase(child);}
        if(file.exists()&&!file.delete())throw new IOException("No se pudo limpiar la descarga");
    }
}
