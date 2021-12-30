package duck.cameras.android.service;

import android.content.Context;
import android.content.SharedPreferences;

import duck.cameras.android.model.LoginToken;
import duck.cameras.android.model.SettingsUrl;

public class LocalSettingsManager {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("duck.cameras.android.service.LocalSettings", Context.MODE_PRIVATE);
    }

    public static LoginToken loadLoginToken(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return new LoginToken(sharedPreferences.getString("loginToken", null));
    }

    public static void saveLoginToken(Context context, LoginToken loginToken) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit().putString("loginToken", loginToken.value()).apply();
    }

    public static SettingsUrl loadSettingsUrl(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return new SettingsUrl(sharedPreferences.getString("settingsUrl", null));
    }

    public static void saveSettingsUrl(Context context, SettingsUrl settingsUrl) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit().putString("settingsUrl", settingsUrl.value()).apply();
    }

    public static boolean isComplete(Context context) {
        return !loadLoginToken(context).isEmpty()
                && !loadSettingsUrl(context).isEmpty();
    }
}
