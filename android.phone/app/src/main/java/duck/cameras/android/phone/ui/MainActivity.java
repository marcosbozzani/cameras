package duck.cameras.android.phone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import duck.cameras.android.model.Camera;
import duck.cameras.android.phone.BuildConfig;
import duck.cameras.android.phone.R;
import duck.cameras.android.service.CameraFinder;
import duck.cameras.android.util.ThreadUtils;

public class MainActivity extends AppCompatActivity {

    private CameraFinder cameraFinder;
    private CamerasAdapter adapter;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);

        adapter = new CamerasAdapter(camera -> {
            Intent intent = new Intent(this, StreamActivity.class);
            intent.putExtra("camera", camera);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        cameraFinder = new CameraFinder(getResources());

        load();
    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ThreadUtils.runOnUiThread(adapter::update);
            }
        }, 2000, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.btn_reload).getIcon().setAlpha(230);
        menu.findItem(R.id.btn_version).getIcon().setAlpha(230);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_reload) {
            load();
        } else if (id == R.id.btn_version) {
            Toast.makeText(this, BuildConfig.BUILD_TIME, Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void load() {
        loading();
        cameraFinder.findAsync(result -> {
            ThreadUtils.runOnUiThread(() -> {
                if (result.ok()) {
                    List<Camera> cameras = result.value();
                    adapter.clear();
                    for (Camera camera : cameras) {
                        adapter.add(camera);
                    }
                } else {
                    String message = result.error().getMessage();
                    Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
                }
                loaded();
            }, 100);
        });
    }

    private void loading() {
        findViewById(R.id.main_spinner).setVisibility(View.VISIBLE);
    }

    private void loaded() {
        findViewById(R.id.main_spinner).setVisibility(View.GONE);
    }

    public static class CamerasAdapter extends Adapter<CamerasAdapter.ViewHolder> {

        private final List<Camera> items = new ArrayList<>();
        private final ClickListener clickListener;

        public CamerasAdapter(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_layout, parent, false);
            return new ViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Camera camera = items.get(position);
            holder.textView.setText(camera.endPoint);
            holder.cardView.setOnClickListener(view -> clickListener.onClick(camera));
            Glide.with(holder.cardView.getContext())
                    .load(camera.profiles.get(0).snapshotUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .signature(new ObjectKey(UUID.randomUUID()))
                    .placeholder(holder.imageView.getDrawable())
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void add(Camera camera) {
            items.add(camera);
            notifyItemRangeChanged(items.size(), 1);
        }

        public void clear() {
            int size = items.size();
            if (size == 0) {
                return;
            }
            items.clear();
            notifyItemRangeRemoved(0, size);
        }

        public void update() {
            notifyItemRangeChanged(0, items.size());
        }

        public interface ClickListener {
            void onClick(Camera camera);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final CardView cardView;
            final TextView textView;
            final ImageView imageView;

            public ViewHolder(CardView cardView) {
                super(cardView);
                this.cardView = cardView;
                textView = cardView.findViewById(R.id.text_view);
                imageView = cardView.findViewById(R.id.image_view);
            }
        }
    }
}