package duck.cameras.android.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class AppUtils {
    public static void exit(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTasks) {
            appTask.finishAndRemoveTask();
        }
    }
}
