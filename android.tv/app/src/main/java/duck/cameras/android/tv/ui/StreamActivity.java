package duck.cameras.android.tv.ui;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import duck.cameras.android.tv.R;
import duck.cameras.android.model.Camera;
import duck.cameras.android.model.PtzData;
import duck.cameras.android.model.Result;
import duck.cameras.android.service.CameraController;
import duck.cameras.android.service.ResourceLoader;
import duck.cameras.android.service.VlcPlayer;
import duck.cameras.android.service.VlcPlayer.Event;
import duck.cameras.android.service.VlcPlayer.EventListener;

public class StreamActivity extends FragmentActivity implements EventListener {

    private VlcPlayer player;
    private SurfaceView surface;
    private ProgressBar spinner;
    private CameraController cameraController;
    private Camera.Profile profile;
    private Camera camera;
    private PtzData ptzData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_activity);

        surface = findViewById(R.id.stream_surface);
        spinner = findViewById(R.id.stream_spinner);

        camera = (Camera) getIntent().getSerializableExtra("camera");
        profile = camera.profiles.get(0);
        cameraController = new CameraController(new ResourceLoader(getResources()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        surface.setFocusable(true);
        surface.post(() -> { //wait for layout measures
            showSpinner();

            player = new VlcPlayer(this, this, surface);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                cameraController.moveLeft(ptzData);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                cameraController.moveRight(ptzData);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                cameraController.moveUp(ptzData);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                cameraController.moveDown(ptzData);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onVlcPlayerEvent(Event event) {
        if (event == Event.STREAM_STARTED) {
            hideSpinner();
        }
    }

    private void showSpinner() {
        int size = (int) (surface.getHeight() * 0.15);
        spinner.getLayoutParams().width = size;
        spinner.getLayoutParams().height = size;
        spinner.setVisibility(View.VISIBLE);
    }

    private void hideSpinner() {
        spinner.setVisibility(View.GONE);
    }
}
