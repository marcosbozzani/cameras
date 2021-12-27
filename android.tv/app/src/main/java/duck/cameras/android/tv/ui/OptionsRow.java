package duck.cameras.android.tv.ui;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.Presenter;

import duck.cameras.android.tv.BuildConfig;
import duck.cameras.android.tv.R;
import duck.cameras.android.model.Action;
import duck.cameras.android.model.Option;
import duck.cameras.android.util.AppUtils;

public class OptionsRow extends ListRow {
    private final Context context;
    private final Action reloadAction;


    public OptionsRow(Context context, Action reloadAction) {
        super(createHeader(), createAdapter());
        this.context = context;
        this.reloadAction = reloadAction;

        ArrayObjectAdapter adapter = (ArrayObjectAdapter) getAdapter();
        adapter.add(new Option("Reload", R.drawable.reload));
        adapter.add(new Option("Version", R.drawable.version));
        adapter.add(new Option("Exit", R.drawable.exit));
    }

    @NonNull
    private static HeaderItem createHeader() {
        return new HeaderItem("Options");
    }

    @NonNull
    private static ArrayObjectAdapter createAdapter() {
        return new ArrayObjectAdapter(new OptionPresenter());
    }

    public void itemClicked(Option option) {
        switch(option.getName()) {
            case "Reload":
                reloadAction.execute();
                break;
            case "Version":
                Toast.makeText(context, BuildConfig.BUILD_TIME, Toast.LENGTH_LONG).show();
                break;
            case "Exit":
                AppUtils.exit(context);
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid option %s", option));
        }
    }

    public static class OptionPresenter extends Presenter {
        private Context context;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            context = parent.getContext();

            ImageCardView card = new ImageCardView(context);
            card.setMainImageDimensions(toDip(150), toDip(100));

            ImageView image = card.getMainImageView();
            image.setPadding(toDip(20), toDip(20), toDip(20), toDip(20));
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);

            TextView title = card.findViewById(R.id.title_text);
            title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            View content = card.findViewById(R.id.content_text);
            content.setVisibility(View.GONE);

            return new ViewHolder(card);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            Option option =  (Option) item;
            ImageCardView card = (ImageCardView) viewHolder.view;
            card.setTitleText(option.getName());
            card.setMainImage(context.getDrawable(option.getIconID()));
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
            ImageCardView card = (ImageCardView) viewHolder.view;
            card.setMainImage(null);
        }

        private int toDip(int value) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                    context.getResources().getDisplayMetrics());
        }
    }
}
