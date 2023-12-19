package duck.cameras.android.phone.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import duck.cameras.android.model.Camera;
import duck.cameras.android.phone.R;
import duck.cameras.android.service.CameraFinder;
import duck.cameras.android.service.LocalSettingsManager;
import duck.cameras.android.service.NetworkService;
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

        adapter = new CamerasAdapter(this, camera -> {
            Intent intent = new Intent(this, StreamActivity.class);
            intent.putExtra("camera", camera);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        cameraFinder = new CameraFinder(getResources());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!LocalSettingsManager.isComplete(this)) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return;
        }
        load(false);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ThreadUtils.runOnUiThread(adapter::update);
            }
        }, 0, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btn_reload) {
            load(true);
        } else if (id == R.id.btn_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void load(boolean forceUpdate) {
        loading();
        cameraFinder.findFromSettingsAsync(this, forceUpdate, result -> {
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
        private final Context context;

        public CamerasAdapter(Context context, ClickListener clickListener) {
            this.context = context;
            this.clickListener = clickListener;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_layout, parent, false);
            return new ViewHolder(cardView);
        }

        @Override
        public long getItemId(int position) {
            Camera camera = items.get(position);
            long id = NetworkService.ipToLong(camera.endPoint);
            return id;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            Camera camera = items.get(position);
            TextView textView = viewHolder.textView;
            CardView cardView = viewHolder.cardView;
            ImageView imageView = viewHolder.imageView;

            textView.setText(camera.name == null ? camera.endPoint : camera.name);
            cardView.setOnClickListener(view -> clickListener.onClick(camera));
            if (cardView.getTag() == null) {
                cardView.setTag("loading");
                String snapshotUri = camera.profiles.get(0).snapshotUri;
                Glide.with(context).clear(imageView);
                Glide.with(context)
                        .load(NetworkService.processUrl(snapshotUri))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .signature(new ObjectKey(UUID.randomUUID()))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource,
                                                        @Nullable Transition<? super Drawable> transition) {
                                cardView.setTag(null);
                                imageView.setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                cardView.setTag(null);
                                imageView.setImageDrawable(null);
                            }
                        });
            }
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