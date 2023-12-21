package duck.cameras.android.tv.ui;

import android.app.Application;

import duck.cameras.android.util.AppUtils;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.registerUncaughtExceptionHandler(getApplicationContext());
    }
}
