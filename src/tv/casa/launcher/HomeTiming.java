package tv.casa.launcher;

import android.os.SystemClock;
import android.util.Log;

/** One timing pair per Home redirect, with no screen content or application data. */
final class HomeTiming {
    private static long started = -1;
    private static int sequence;

    static void requested(long eventTime) {
        started = SystemClock.uptimeMillis();
        sequence++;
        Log.i("CasaTiming", "home_request id=" + sequence
            + " eventDeliveryMs=" + Math.max(0, started - eventTime));
    }

    static void focused() {
        if (started < 0) return;
        Log.i("CasaTiming", "home_focused id=" + sequence
            + " redirectMs=" + (SystemClock.uptimeMillis() - started));
        started = -1;
    }
}
