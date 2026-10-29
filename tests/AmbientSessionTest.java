package tv.casa.launcher;
public final class AmbientSessionTest {
    static void check(boolean b){if(!b)throw new AssertionError();}
    public static void main(String[] args){AmbientSession s=new AmbientSession();check(s.state()==AmbientSession.State.STOPPED);s.begin();int first=s.prepare();check(s.ready(first));check(!s.ready(first));check(s.fallback(first));check(!s.ready(first));check(!s.fallback(first));check(s.state()==AmbientSession.State.FALLBACK);s.stop();check(!s.ready(first));s.begin();int second=s.prepare();check(second!=first);check(!s.ready(first));check(s.ready(second));s.stop();check(!s.fallback(second));System.out.println("Ambient session passed: first frame, surface fallback, stale callbacks and stop/restart.");}
}
