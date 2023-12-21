package duck.cameras.android.tv.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.Presenter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;

import java.util.List;
import java.util.UUID;

import duck.cameras.android.model.Camera;
import duck.cameras.android.model.LoadListener;
import duck.cameras.android.model.Result;
import duck.cameras.android.service.CameraFinder;
import duck.cameras.android.service.NetworkService;
import duck.cameras.android.util.ThreadUtils;

public class CamerasRow extends ListRow {
    private final Context context;
    private final LoadListener loadListener;
    private final ArrayObjectAdapter adapter;
    private final CameraFinder cameraFinder;

    public CamerasRow(Context context, LoadListener loadListener) {
        super(createHeader(), createAdapter());
        this.context = context;
        this.loadListener = loadListener;
        this.adapter = (ArrayObjectAdapter) getAdapter();
        this.cameraFinder = new CameraFinder(context.getResources());
    }

    @NonNull
    private static HeaderItem createHeader() {
        return new HeaderItem("Cameras");
    }

    @NonNull
    private static ArrayObjectAdapter createAdapter() {
        return new ArrayObjectAdapter(new CameraPresenter());
    }

    public void load(boolean forceUpdate) {
        loadListener.loading();
        cameraFinder.findFromSettingsAsync(context, forceUpdate, this::processFindResult);
    }

    public void update() {
        adapter.notifyItemRangeChanged(0, adapter.size());
    }

    private void processFindResult(Result<List<Camera>> result) {
        ThreadUtils.runOnUiThread(() -> {
            if (result.ok()) {
                List<Camera> cameras = result.value();
                adapter.clear();
                for (Camera camera : cameras) {
                    adapter.add(camera);
                }
            } else {
                String message = result.error().getMessage();
                Toast.makeText(context, "Error: " + message, Toast.LENGTH_LONG).show();
            }
            loadListener.loaded();
        }, 100);
    }

    public void itemClicked(Camera camera) {
        Intent intent = new Intent(context, StreamActivity.class);
        intent.putExtra("camera", camera);
        context.startActivity(intent);
    }

    public static class CameraPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            ImageCardView card = new ImageCardView(parent.getContext());
            card.setFocusable(true);
            card.setFocusableInTouchMode(true);
            return new ViewHolder(card);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            final int width = 16 * 35;
            final int height = 9 * 35;
            Camera camera = (Camera) item;
            ImageCardView card = (ImageCardView) viewHolder.view;
            card.setTitleText(camera.name == null ? camera.endPoint : camera.name);
            card.setMainImageDimensions(width, height);

            if (card.getTag() == null) {
                card.setTag("loading");
                String snapshotUri = camera.profiles.get(0).snapshotUri;
                Glide.with(card.getContext()).clear(card.getMainImageView());
                Glide.with(card.getContext())
                        .load(NetworkService.processUrl(snapshotUri))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .signature(new ObjectKey(UUID.randomUUID()))
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource,
                                                        @Nullable Transition<? super Drawable> transition) {
                                card.setTag(null);
                                card.getMainImageView().setImageDrawable(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                card.setTag(null);
                                card.getMainImageView().setImageDrawable(null);
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                card.setTag(null);
                                card.getMainImageView().setImageDrawable(null);
                            }
                        });
            }
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
            ImageCardView card = (ImageCardView) viewHolder.view;
            card.setMainImage(null);
        }
    }
}
