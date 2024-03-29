package duck.cameras.android.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import duck.cameras.android.model.Callback;
import duck.cameras.android.model.Result;
import duck.cameras.android.model.Settings;
import duck.cameras.android.model.SettingsUrl;
import duck.cameras.android.util.ThreadUtils;

public class SettingsLoader {

    public static Settings load(Context context, boolean update) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences("duck.cameras.android.service.Settings", Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("data", null);
        if (data == null || update) {
            SettingsUrl settingsUrl = LocalSettingsManager.getSettingsUrl(context);
            if (settingsUrl.isEmpty()) {
                throw new RuntimeException("settingsUrl is null");
            }
            data = NetworkService.httpGet(settingsUrl.value());
            sharedPreferences.edit().putString("data", data).apply();
        }
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Settings settings = mapper.readValue(data, Settings.class);
            NetworkService.setMode(settings);
            return settings;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadAsync(Context context, boolean update, Callback<Settings> callback) {
        new Thread(() -> {
            try {
                Settings settings = SettingsLoader.load(context, update);
                ThreadUtils.runOnUiThread(() -> {
                    callback.execute(Result.ok(settings));
                });
            } catch (Exception e) {
                ThreadUtils.runOnUiThread(() -> {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    callback.execute(Result.error(null, e));
                });
            }
        }).start();
    }
}
