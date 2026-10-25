package tv.casa.launcher;

import java.net.URI;

final class GamePolicy {
    static final int LIMIT=8;
    static final String[] NAMES={"TIC-80 · juegos de la comunidad","PICO-8 · cartuchos","DOS Zone · clásicos de PC"};
    static final String[] URLS={"https://tic80.com/play/games/top","https://www.lexaloffle.com/bbs/?cat=7","https://dos.zone/"};
    static boolean safeUrl(String text){
        if(text==null||text.length()>2048)return false;
        try{URI u=new URI(text);String h=u.getHost();return "https".equalsIgnoreCase(u.getScheme())&&h!=null&&!h.isEmpty()&&u.getRawUserInfo()==null&&(u.getPort()==-1||u.getPort()==443);}
        catch(Exception e){return false;}
    }
    static String title(String text){String s=text==null?"Juego web":text.replaceAll("[\\p{Cntrl}]", " ").trim();return s.isEmpty()?"Juego web":s.substring(0,Math.min(80,s.length()));}
}
