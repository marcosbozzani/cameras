package duck.cameras.android.tv.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import duck.cameras.android.model.Camera;
import duck.cameras.android.model.LoadListener;
import duck.cameras.android.service.LocalSettingsManager;
import duck.cameras.android.tv.R;
import duck.cameras.android.tv.model.Option;
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
        private boolean entranceDone = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setHeadersState(HEADERS_HIDDEN);
            setHeadersTransitionOnBackEnabled(true);
            setBrandColor(ContextCompat.getColor(requireContext(), R.color.app_launcher_background));
            setOnItemViewClickedListener(this::itemClicked);

            Context context = requireContext();
            camerasRow = new CamerasRow(context, new ListRowLoadListener());
            OptionsRow optionsRow = new OptionsRow(context, () -> camerasRow.load(true));

            ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ListRowPresenter());
            adapter.add(camerasRow);
            adapter.add(optionsRow);
            setAdapter(adapter);

            prepareEntranceTransition();
        }

        @Override
        public void onResume() {
            super.onResume();

            if (!LocalSettingsManager.isComplete(getContext())) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                return;
            }

            camerasRow.load(false);

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    ThreadUtils.runOnUiThread(camerasRow::update);
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

        public class ListRowLoadListener implements LoadListener {
            @Override
            public void loading() {
                if (entranceDone) {
                    showSpinner();
                }
            }

            @Override
            public void loaded() {
                if (!entranceDone) {
                    startEntranceTransition();
                    entranceDone = true;
                } else{
                    hideSpinner();
                }
            }
        }

        private void showSpinner() {
            getSpinner().bringToFront();
            getSpinner().setVisibility(View.VISIBLE);
        }

        private void hideSpinner() {
            getSpinner().setVisibility(View.GONE);
        }

        private View getSpinner() {
            return Objects.requireNonNull(getActivity()).findViewById(R.id.main_spinner);
        }

        public void itemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (row instanceof CamerasRow) {
                ((CamerasRow) row).itemClicked((Camera) item);
            } else if (row instanceof OptionsRow) {
                ((OptionsRow) row).itemClicked((Option) item);
            } else {
                throw new IllegalArgumentException(String.format("Invalid row %s", row));
            }
        }
    }
}
