package tv.casa.launcher;
public final class GamePolicyTest {
    static void check(boolean ok){if(!ok)throw new AssertionError();}
    public static void main(String[] args){
        for(String url:GamePolicy.URLS)check(GamePolicy.safeUrl(url));
        for(String bad:new String[]{"http://example.com","file:///etc/passwd","content://test/x","javascript:alert(1)","intent://x","https://user:pass@example.com","https://example.com:8080","https://","https://example.com/\n","//example.com",null})check(!GamePolicy.safeUrl(bad));
        check(!GamePolicy.safeUrl("https://example.com/"+new String(new char[2048]).replace('\0','a')));
        check(GamePolicy.safeUrl("https://example.com:443/game?q=1#play"));
        check(GamePolicy.title(new String(new char[100]).replace('\0','a')).length()==80);
        check(GamePolicy.title("\n\r").equals("Juego web"));check(GamePolicy.LIMIT==8);
        System.out.println("Game policy passed: HTTPS, blocked schemes/credentials/ports, bounded URL and title.");
    }
}
