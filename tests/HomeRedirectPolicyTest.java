package tv.casa.launcher;

public final class HomeRedirectPolicyTest {
    private static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    public static void main(String[] args){
        check(HomeRedirectPolicy.isHome("com.google.android.apps.tv.launcherx","com.google.android.apps.tv.launcherx.home.HomeActivity"),"Google Home must match");
        check(HomeRedirectPolicy.isHome("com.spocky.projengmenu","com.spocky.projengmenu.ui.home.MainActivity"),"Projectivy Home must match");
        check(!HomeRedirectPolicy.isHome("unrelated.app","com.google.android.apps.tv.launcherx.home.HomeActivity"),"Do not redirect another package");
        check(!HomeRedirectPolicy.isHome("com.google.android.apps.tv.launcherx","another.Activity"),"Do not redirect other activities");
        HomeRedirectPolicy gate=new HomeRedirectPolicy();
        check(gate.accept(0),"First event at uptime zero must work");
        check(!gate.accept(100),"Duplicate event must be coalesced");
        check(gate.accept(150),"Do not retain the old 1200 ms lockout");
        check(gate.accept(500),"A subsequent real Home press must work");
        System.out.println("Home redirect tests passed: scope, duplicates and rapid subsequent Home.");
    }
}
