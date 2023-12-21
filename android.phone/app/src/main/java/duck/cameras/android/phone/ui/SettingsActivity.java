package duck.cameras.android.phone.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import duck.cameras.android.model.LoginToken;
import duck.cameras.android.model.SettingsUrl;
import duck.cameras.android.phone.BuildConfig;
import duck.cameras.android.phone.databinding.SettingsActivityBinding;
import duck.cameras.android.service.LocalSettingsManager;
import duck.cameras.android.service.SettingsLoader;
import duck.cameras.android.util.ThreadUtils;

public class SettingsActivity extends AppCompatActivity {
    private SettingsActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoginToken loginToken = LocalSettingsManager.getLoginToken(this);
        SettingsUrl settingsUrl = LocalSettingsManager.getSettingsUrl(this);

        binding.txtVersion.setText(String.format("%s (%s)", BuildConfig.BUILD_TIME, BuildConfig.BUILD_TYPE));
        binding.edtToken.setText(loginToken.value());
        binding.edtSettingsUrl.setText(settingsUrl.value());

        binding.btnSave.setOnClickListener(view -> {
            String loginTokenText = binding.edtToken.getText().toString();
            String settingsUrlText = binding.edtSettingsUrl.getText().toString();

            LocalSettingsManager.save(this, new LoginToken(loginTokenText), new SettingsUrl(settingsUrlText));

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
