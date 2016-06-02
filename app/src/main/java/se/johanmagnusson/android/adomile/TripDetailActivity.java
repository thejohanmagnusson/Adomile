package se.johanmagnusson.android.adomile;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import se.johanmagnusson.android.adomile.database.TripProvider;

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

            getSupportFragmentManager().beginTransaction().add(R.id.detail_container, fragment, TripDetailFragment.TAG).commit();
        }
    }

    private void deleteTrip() {
        TripDetailFragment fragment = (TripDetailFragment) getSupportFragmentManager().findFragmentByTag(TripDetailFragment.TAG);

        if(fragment != null) {
            long tripId = fragment.getTripId();
            Uri uri = TripProvider.Trips.withId(tripId);

            getContentResolver().delete(uri, null, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
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
        else if (id == R.id.action_delete) {
            deleteTrip();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
