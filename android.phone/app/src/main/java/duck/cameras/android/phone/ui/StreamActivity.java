package duck.cameras.android.phone.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import duck.cameras.android.model.Action;
import duck.cameras.android.model.Camera;
import duck.cameras.android.model.Preset;
import duck.cameras.android.model.Settings;
import duck.cameras.android.phone.databinding.StreamActivityBinding;
import duck.cameras.android.service.CameraController;
import duck.cameras.android.service.ResourceLoader;
import duck.cameras.android.service.SettingsLoader;
import duck.cameras.android.service.VlcPlayer;

public class StreamActivity extends Activity implements VlcPlayer.EventListener {

    private VlcPlayer player;
    private StreamActivityBinding binding;
    private CameraController cameraController;
    private Camera.Profile profile;
    private Camera camera;
    private Settings.EndPoint endPoint;
    private boolean buttonsVisible = true;
    private List<Preset> presets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = StreamActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        hideButtons();
        hideStatusBar();

        camera = (Camera) getIntent().getSerializableExtra("camera");
        profile = camera.profiles.get(0);

        SettingsLoader.loadAsync(this, false, result -> {
            if (result.ok()) {
                Settings settings = result.value();
                endPoint = settings.getEndPoint(camera.endPoint);
                presets = createPresetList(endPoint.presets);
                cameraController = new CameraController(new ResourceLoader(getResources()));
                setClickListeners();
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
        //menu.setHeaderTitle("Context Menu");
        menu.add("Back");
        menu.add(player.isMuted() ? "Un-Mute" : "Mute");
        for (int i = 0; i < presets.size(); i++) {
            menu.add(0, 1000 + i, 0, presets.get(i).name);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Back") {
            finish();
        }
        else if (item.getTitle() == "Mute") {
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
        binding.streamSurface.setFocusable(true);
        binding.streamSurface.post(() -> { //wait for layout measures
            showSpinner();
            player = new VlcPlayer(this, this, binding.streamSurface);
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
    public void onVlcPlayerEvent(VlcPlayer.Event event) {
        if (event == VlcPlayer.Event.STREAM_STARTED) {
            hideSpinner();
        }
    }

    private void setClickListeners() {
        directionButtonEvents(binding.btnLeft, () -> {
            cameraController.moveLeft(camera.endPoint, profile.token, endPoint.speed);
        });
        directionButtonEvents(binding.btnRight, () -> {
            cameraController.moveRight(camera.endPoint, profile.token, endPoint.speed);
        });
        directionButtonEvents(binding.btnUp, () -> {
            cameraController.moveUp(camera.endPoint, profile.token, endPoint.speed);
        });
        directionButtonEvents(binding.btnDown, () -> {
            cameraController.moveDown(camera.endPoint, profile.token, endPoint.speed);
        });

        registerForContextMenu(binding.streamSurface);
        binding.streamSurface.setOnClickListener(view -> {
            if (buttonsVisible) {
                hideButtons();
            } else {
                showButtons();
            }
        });
    }

    private void directionButtonEvents(View button, Action action) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    action.execute();
                    return true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                        motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    cameraController.stop(camera.endPoint, profile.token);
                    return true;
                }
                return false;
            }
        });
    }

    private void hideStatusBar() {
        getActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
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
