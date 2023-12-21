package duck.cameras.android.tv.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import duck.cameras.android.model.LoginToken;
import duck.cameras.android.model.SettingsUrl;
import duck.cameras.android.service.LocalSettingsManager;
import duck.cameras.android.service.SettingsLoader;
import duck.cameras.android.tv.BuildConfig;
import duck.cameras.android.tv.databinding.SettingsActivityBinding;
import duck.cameras.android.util.ThreadUtils;

public class SettingsActivity extends FragmentActivity {

    private SettingsActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoginToken loginToken = LocalSettingsManager.loadLoginToken(this);
        SettingsUrl settingsUrl = LocalSettingsManager.loadSettingsUrl(this);

        binding.txtVersion.setText(String.format("%s (%s)", BuildConfig.BUILD_TIME, BuildConfig.BUILD_TYPE));
        binding.edtToken.setText(loginToken.value());
        binding.edtSettingsUrl.setText(settingsUrl.value());

        binding.btnSave.setOnClickListener(view -> {
            String loginTokenText = binding.edtToken.getText().toString();
            LocalSettingsManager.saveLoginToken(this, new LoginToken(loginTokenText));

            String settingsUrlText = binding.edtSettingsUrl.getText().toString();
            LocalSettingsManager.saveSettingsUrl(this, new SettingsUrl(settingsUrlText));

            new Thread(() -> {
                try {
                    SettingsLoader.load(this, true);
                    ThreadUtils.runOnUiThread(() -> {
                        finish();
                    });

                } catch (Exception e) {
                    ThreadUtils.runOnUiThread(() -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();

        });
    }

}
