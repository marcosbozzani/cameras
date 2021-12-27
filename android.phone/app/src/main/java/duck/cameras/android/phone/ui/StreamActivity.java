package duck.cameras.android.phone.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import duck.cameras.android.model.Camera;
import duck.cameras.android.model.PtzData;
import duck.cameras.android.model.Result;
import duck.cameras.android.phone.databinding.StreamActivityBinding;
import duck.cameras.android.service.CameraController;
import duck.cameras.android.service.ResourceLoader;
import duck.cameras.android.service.VlcPlayer;

public class StreamActivity extends Activity implements VlcPlayer.EventListener {

    private VlcPlayer player;
    private StreamActivityBinding binding;
    private CameraController cameraController;
    private Camera.Profile profile;
    private Camera camera;
    private PtzData ptzData;
    private boolean buttonsVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StreamActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        camera = (Camera) getIntent().getSerializableExtra("camera");
        profile = camera.profiles.get(0);
        cameraController = new CameraController(new ResourceLoader(getResources()));

        hideButtons();
        setClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.streamSurface.setFocusable(true);
        binding.streamSurface.post(() -> { //wait for layout measures
            showSpinner();

            player = new VlcPlayer(this, this, binding.streamSurface);
            player.play(profile.streamUri);

            cameraController.loadAsync(camera, profile, (Result<PtzData> result) -> {
                if (result.ok()) {
                    ptzData = result.value();
                }
            });
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    public void onVlcPlayerEvent(VlcPlayer.Event event) {
        if (event == VlcPlayer.Event.STREAM_STARTED) {
            hideSpinner();
        }
    }

    private void setClickListeners() {
        binding.btnLeft.setOnClickListener(view -> cameraController.moveLeft(ptzData));
        binding.btnRight.setOnClickListener(view -> cameraController.moveRight(ptzData));
        binding.btnUp.setOnClickListener(view -> cameraController.moveUp(ptzData));
        binding.btnDown.setOnClickListener(view -> cameraController.moveDown(ptzData));
        binding.streamSurface.setOnClickListener(view -> {
            if (buttonsVisible) {
                hideButtons();
            } else {
                showButtons();
            }
        });
    }

    private void hideButtons() {
        buttonsVisible = false;
        binding.btnLeft.setVisibility(View.GONE);
        binding.btnRight.setVisibility(View.GONE);
        binding.btnUp.setVisibility(View.GONE);
        binding.btnDown.setVisibility(View.GONE);
    }

    private void showButtons() {
        buttonsVisible = true;
        binding.btnLeft.setVisibility(View.VISIBLE);
        binding.btnRight.setVisibility(View.VISIBLE);
        binding.btnUp.setVisibility(View.VISIBLE);
        binding.btnDown.setVisibility(View.VISIBLE);
    }

    private void showSpinner() {
        int size = (int) (binding.streamSurface.getHeight() * 0.15);
        binding.streamSpinner.getLayoutParams().width = size;
        binding.streamSpinner.getLayoutParams().height = size;
        binding.streamSpinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        binding.streamSpinner.setVisibility(View.GONE);
    }
}
