package duck.cameras.android.tv.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import java.util.Timer;
import java.util.TimerTask;

import duck.cameras.android.tv.R;
import duck.cameras.android.model.Camera;
import duck.cameras.android.model.LoadListener;
import duck.cameras.android.model.Option;
import duck.cameras.android.util.ThreadUtils;

public class MainActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, new MainFragment())
                    .commit();
        }
    }

    public static class MainFragment extends BrowseSupportFragment {
        private Timer timer;
        private CamerasRow camerasRow;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setHeadersState(HEADERS_HIDDEN);
            setHeadersTransitionOnBackEnabled(true);
            setBrandColor(getResources().getColor(R.color.app_launcher_background));
            setOnItemViewClickedListener(this::itemClicked);

            Context context = requireContext();
            camerasRow = new CamerasRow(context, new ListRowLoadListener());
            OptionsRow optionsRow = new OptionsRow(context, camerasRow::load);

            ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ListRowPresenter());
            adapter.add(camerasRow);
            adapter.add(optionsRow);
            setAdapter(adapter);

            camerasRow.load();
        }

        @Override
        public void onResume() {
            super.onResume();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    ThreadUtils.runOnUiThread(camerasRow::update);
                }
            }, 2000, 500);
        }

        @Override
        public void onPause() {
            super.onPause();
            timer.cancel();
        }

        public class ListRowLoadListener implements LoadListener {
            @Override
            public void loading() {
                prepareEntranceTransition();
            }

            @Override
            public void loaded() {
                startEntranceTransition();
            }
        }

        public void itemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (row instanceof CamerasRow) {
                ((CamerasRow) row).itemClicked((Camera) item);
            } else if (row instanceof OptionsRow) {
                ((OptionsRow) row).itemClicked((Option) item);
            }
            else {
                throw new IllegalArgumentException(String.format("Invalid row %s", row));
            }
        }
    }
}
