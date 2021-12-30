package duck.cameras.android.service;

import static duck.cameras.android.model.Settings.EndPoint;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

import duck.cameras.android.model.Settings;
import duck.cameras.android.model.SettingsUrl;

public class SettingsLoader {

    public static Settings load(Context context, boolean update) {
        SharedPreferences sharedPreferences = context
                .getSharedPreferences("duck.cameras.android.service.Settings", Context.MODE_PRIVATE);
        String data = sharedPreferences.getString("data", null);
        if (data == null || update) {
            SettingsUrl settingsUrl = LocalSettingsManager.loadSettingsUrl(context);
            if (settingsUrl.isEmpty()) {
                throw new RuntimeException("settingsUrl is null");
            }
            data = NetworkService.httpGet(settingsUrl.value());
            sharedPreferences.edit().putString("data", data).apply();
        }
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(data, Settings.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
