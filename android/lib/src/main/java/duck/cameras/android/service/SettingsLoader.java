package duck.cameras.android.service;

import static duck.cameras.android.model.Settings.EndPoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
            SettingsUrl settingsUrl = LocalSettingsManager.loadSettingsUrl(context);
            if (settingsUrl.isEmpty()) {
                throw new RuntimeException("settingsUrl is null");
            }
            data = NetworkService.httpGet(settingsUrl.value());
            sharedPreferences.edit().putString("data", data).apply();
        }
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Settings settings = mapper.readValue(data, Settings.class);
            for (int i = 0; i < settings.endPoints.size(); i++) {
                EndPoint endPoint = settings.endPoints.get(i);
                String[] splat  = endPoint.host.split("@");
                if (splat.length == 2) endPoint.host = splat[1];
                String hostAddress = InetAddress.getByName(endPoint.host).getHostAddress();
                endPoint.host = hostAddress;
                if (splat.length == 2) endPoint.host = splat[0] + "@" + endPoint.host;
            }
            return settings;
        } catch (JsonProcessingException | UnknownHostException e) {
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
