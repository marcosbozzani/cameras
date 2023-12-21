package duck.cameras.android.tv.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import duck.cameras.android.model.Camera;
import duck.cameras.android.model.Preset;
import duck.cameras.android.model.PtzData;
import duck.cameras.android.model.Result;
import duck.cameras.android.model.Settings;
import duck.cameras.android.service.CameraController;
import duck.cameras.android.service.ResourceLoader;
import duck.cameras.android.service.SettingsLoader;
import duck.cameras.android.service.VlcPlayer;
import duck.cameras.android.service.VlcPlayer.Event;
import duck.cameras.android.service.VlcPlayer.EventListener;
import duck.cameras.android.tv.R;

public class StreamActivity extends FragmentActivity implements EventListener {

    private VlcPlayer player;
    private SurfaceView surface;
    private ProgressBar spinner;
    private CameraController cameraController;
    private Camera.Profile profile;
    private Camera camera;
    private Settings.EndPoint endPoint;
    private List<Preset> presets;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream_activity);

        surface = findViewById(R.id.stream_surface);
        spinner = findViewById(R.id.stream_spinner);

        camera = (Camera) getIntent().getSerializableExtra("camera");
        profile = camera.profiles.get(0);

        SettingsLoader.loadAsync(this, false, result -> {
            if (result.ok()) {
                endPoint = result.value().getEndPoint(camera.endPoint);
                presets = createPresetList(endPoint.presets);
                cameraController = new CameraController(new ResourceLoader(getResources()));

                registerForContextMenu(surface);
            } else {
                Log.e(StreamActivity.class.getSimpleName(), "SettingsLoader.loadAsync failed");
            }
        });
    }

    private List<Preset> createPresetList(Map<String, String> presets) {
        List<String> sortedKeys = new ArrayList<>(presets.keySet());
        Collections.sort(sortedKeys);

        ArrayList<Preset> list = new ArrayList<>();
        for (String key : sortedKeys) {
            Preset preset = new Preset();
            preset.token = key;
            preset.name = presets.get(key);
            list.add(preset);
        }
        return list;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(player.isMuted() ? "Un-Mute" : "Mute");
        for (int i = 0; i < presets.size(); i++) {
            menu.add(0, 1000 + i, 0, presets.get(i).name);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Mute") {
            player.mute();
        }
        else if (item.getTitle() == "Un-Mute") {
            player.unmute();
        }
        else {
            int id = item.getItemId() - 1000;
            if (id >= 0 && id < presets.size()) {
                String presetToken = presets.get(id).token;
                cameraController.gotoPreset(camera.endPoint, profile.token, presetToken);
            } else {
                return  false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        surface.setFocusable(true);
        surface.post(() -> { //wait for layout measures
            showSpinner();

            player = new VlcPlayer(this, this, surface);
            player.mute();
            player.play(profile.streamUri);
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
                cameraController.moveLeft(camera.endPoint, profile.token, endPoint.speed);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                cameraController.moveRight(camera.endPoint, profile.token, endPoint.speed);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                cameraController.moveUp(camera.endPoint, profile.token, endPoint.speed);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                cameraController.moveDown(camera.endPoint, profile.token, endPoint.speed);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                cameraController.stop(camera.endPoint, profile.token);
                break;
        }
        return super.onKeyUp(keyCode, event);
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
