package tv.casa.launcher;
/** Session tokens reject callbacks from an old surface/player or a stopped scene. */
final class AmbientSession {
    enum State {PHOTO,PREPARING,VIDEO,FALLBACK,STOPPED}
    private State state=State.STOPPED;private int token;
    State state(){return state;}
    void begin(){token++;state=State.PHOTO;}
    int prepare(){if(state==State.STOPPED)throw new IllegalStateException();state=State.PREPARING;return ++token;}
    boolean current(int request){return request==token&&state!=State.STOPPED;}
    boolean ready(int request){if(!current(request)||state!=State.PREPARING)return false;state=State.VIDEO;return true;}
    boolean fallback(int request){if(!current(request))return false;token++;state=State.FALLBACK;return true;}
    void stop(){token++;state=State.STOPPED;}
}
