package se.johanmagnusson.android.adomile;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class TripDetailActivity extends AppCompatActivity {

    private static final String TAG = TripDetailActivity.class.getSimpleName();

    private static final long INVALID_ID = -1L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null) {
            long id = getIntent().getLongExtra(TripDetailFragment.TRIP_ID_KEY, INVALID_ID);

            TripDetailFragment fragment = new TripDetailFragment();

            if(id != INVALID_ID) {
                Bundle args = new Bundle();
                args.putLong(TripDetailFragment.TRIP_ID_KEY, id);
                fragment.setArguments(args);
            }

            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                finishAfterTransition();
            else
                finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
