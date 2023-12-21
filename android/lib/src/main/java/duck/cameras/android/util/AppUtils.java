package duck.cameras.android.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import duck.cameras.android.service.LocalSettingsManager;

public class AppUtils {
    public static void exit(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTasks) {
            appTask.finishAndRemoveTask();
        }
    }

    public static void registerUncaughtExceptionHandler(Context context) {
        Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (!(handler instanceof AppUncaughtExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new AppUncaughtExceptionHandler(handler, context));
        }
    }

    private static class AppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private final Thread.UncaughtExceptionHandler handler;
        private final Context context;

        public AppUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler, Context context) {
            this.handler = handler;
            this.context = context;
        }

        @Override
        public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
            try {
                Log.e(AppUtils.class.getSimpleName(), throwable.getMessage(), throwable);
                LocalSettingsManager.resetCompleted(context);
                saveStacktrace(throwable, context);
                if (handler != null) {
                    handler.uncaughtException(thread, throwable);
                } else {
                    System.exit(1);
                }
            } catch (Throwable t) {
                Log.e(AppUtils.class.getSimpleName(), t.getMessage(), t);
            }
        }

        private void saveStacktrace(Throwable throwable, Context context) {
            final StringWriter writer = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            final String stacktrace = writer.toString();

            FileOutputStream stream = null;
            try {
                String pathname = context.getExternalFilesDir(null) + "/stacktrace.log";
                stream = new FileOutputStream(pathname, true);
                stream.write(stacktrace.getBytes());
                stream.write("----------------------------------------\n".getBytes());
                stream.flush();
            } catch (Exception e) {
                Log.e(AppUtils.class.getSimpleName(), e.getMessage(), e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.e(AppUtils.class.getSimpleName(), e.getMessage(), e);
                    }
                }
            }
        }
    }
}
