package duck.cameras.android.util;

import android.os.Build;

public class DeviceUtils {
    private static Boolean runningOnEmulator;

    public static boolean isRunningOnEmulator() {
        if (runningOnEmulator == null) {
            runningOnEmulator = Build.FINGERPRINT.endsWith("test-keys") && Build.MANUFACTURER.equals("unknown")
                    || Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                    && Build.FINGERPRINT.endsWith(":user/release-keys")
                    && Build.MANUFACTURER.equals("Google");
        }
        return runningOnEmulator;
    }
}
