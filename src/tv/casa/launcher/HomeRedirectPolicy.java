package tv.casa.launcher;

/** Small, framework-free gate: coalesce duplicate events without blocking a new Home press. */
final class HomeRedirectPolicy {
    private long lastAccepted = -1;

    static boolean isHome(String packageName, String className) {
        return ("com.google.android.apps.tv.launcherx".equals(packageName)
                && "com.google.android.apps.tv.launcherx.home.HomeActivity".equals(className))
            || ("com.spocky.projengmenu".equals(packageName)
                && "com.spocky.projengmenu.ui.home.MainActivity".equals(className));
    }

    boolean accept(long uptimeMillis) {
        if (lastAccepted >= 0 && uptimeMillis - lastAccepted < 150) return false;
        lastAccepted = uptimeMillis;
        return true;
    }
}
