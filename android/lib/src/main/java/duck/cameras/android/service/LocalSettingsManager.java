package duck.cameras.android.service;

import android.content.Context;
import android.content.SharedPreferences;

import duck.cameras.android.model.LoginToken;
import duck.cameras.android.model.SettingsUrl;

public class LocalSettingsManager {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("duck.cameras.android.service.LocalSettings", Context.MODE_PRIVATE);
    }

    public static LoginToken getLoginToken(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return new LoginToken(sharedPreferences.getString("loginToken", null));
    }

    public static SettingsUrl getSettingsUrl(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return new SettingsUrl(sharedPreferences.getString("settingsUrl", null));
    }

    public static boolean isCompleted(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean("completed", false);
    }

    public static void resetCompleted(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit().putBoolean("completed", false).apply();
    }

    public static void save(Context context, LoginToken loginToken, SettingsUrl settingsUrl) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit()
                .putString("loginToken", loginToken.value())
                .putString("settingsUrl", settingsUrl.value())
                .putBoolean("completed", true)
                .apply();
    }
}
