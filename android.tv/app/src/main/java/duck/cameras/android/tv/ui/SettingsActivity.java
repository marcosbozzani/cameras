package duck.cameras.android.tv.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.net.InetAddress;

import duck.cameras.android.model.LoginToken;
import duck.cameras.android.model.SettingsUrl;
import duck.cameras.android.service.SettingsHttpServer;
import duck.cameras.android.service.LocalSettingsManager;
import duck.cameras.android.service.NetworkService;
import duck.cameras.android.service.SettingsHttpServer.Configuration;
import duck.cameras.android.service.SettingsHttpServer.Data;
import duck.cameras.android.service.SettingsLoader;
import duck.cameras.android.tv.BuildConfig;
import duck.cameras.android.tv.R;
import duck.cameras.android.tv.databinding.SettingsActivityBinding;
import duck.cameras.android.util.QRCodeUtils;
import duck.cameras.android.util.ThreadUtils;

public class SettingsActivity extends FragmentActivity {

    private ProgressBar spinner;
    private SettingsActivityBinding binding;
    private final SettingsHttpServer settingsHttpServer = new SettingsHttpServer();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        spinner = findViewById(R.id.settings_spinner);
        showSpinner();

        LoginToken loginToken = LocalSettingsManager.getLoginToken(this);
        SettingsUrl settingsUrl = LocalSettingsManager.getSettingsUrl(this);

        binding.txtVersion.setText(String.format("%s (%s)", BuildConfig.BUILD_TIME, BuildConfig.BUILD_TYPE));
        binding.edtToken.setText(loginToken.value());
        binding.edtSettingsUrl.setText(settingsUrl.value());

        Data data = new Data();
        data.loginToken = loginToken.value();
        data.settingsUrl = settingsUrl.value();

        Configuration configuration = new Configuration();
        configuration.assetManager = getAssets();
        configuration.ready = result -> {
            hideSpinner();
            if (result.ok()) {
                int port = result.value();
                InetAddress address = NetworkService.getFirstSiteLocalAddress();
                if (address != null) {
                    String serverUrl = "http://" + address.getHostAddress() + ":" + port;
                    binding.txtQrCode.setText(serverUrl);
                    int width = binding.imgQrCode.getWidth();
                    int height = binding.imgQrCode.getHeight();
                    binding.imgQrCode.setImageBitmap(QRCodeUtils.generate(serverUrl, width, height));
                } else {
                    binding.txtQrCode.setText(R.string.error_generating_qr_code);
                }
            }
        };
        configuration.sync = result -> {
            if (result.ok()) {
                Data resultData = result.value();
                binding.edtToken.setText(resultData.loginToken);
                binding.edtSettingsUrl.setText(resultData.settingsUrl);
            }
        };
        settingsHttpServer.start(data, configuration);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settingsHttpServer.stop();
    }

    private void showSpinner() {
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }
}
