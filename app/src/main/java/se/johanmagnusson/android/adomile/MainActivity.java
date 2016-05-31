package se.johanmagnusson.android.adomile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import se.johanmagnusson.android.adomile.database.TripColumns;
import se.johanmagnusson.android.adomile.database.TripProvider;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
                   LogFragment.OnTripSelectedListener,
                   RegisterFragment.OnRegisterTripListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int SUMMARY_PAGE = 0;
    private static final int REGISTER_PAGE = 1;
    private static final int LOG_PAGE = 2;
    private static final String KEY_SELECTED_TAB = "selected_tab";

    public static final String ACTION_WIDGET_UPDATE= "se.johanmagnusson.android.adomile.action.WIDGET_UPDATE";

    private static final String KEY_MONTH = "month";
    private static final String KEY_INBOUND = "inbound";
    private static final String KEY_OUTBOUND = "outbound";
    private static final String KEY_PRIVATE_MILEAGE = "private_mileage";
    private static final String KEY_WORK_MILEAGE = "work_mileage";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position != REGISTER_PAGE){
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

                    try {
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    catch (NullPointerException e){
                        Log.d(TAG, "Error trying to get current focus: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        if(savedInstanceState == null)
            mTabLayout.getTabAt(REGISTER_PAGE).select();
        else
            mTabLayout.getTabAt(savedInstanceState.getInt(KEY_SELECTED_TAB)).select();
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateTripSummary();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_TAB, mTabLayout.getSelectedTabPosition());

        super.onSaveInstanceState(outState);
    }

    // DatePickerFragment/DatePickerDialog.OnDateSetListener
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Safe to cast since only RegisterFragment can call this
        RegisterFragment fragment = (RegisterFragment) mSectionsPagerAdapter.getCurrentFragment();

        if(fragment != null) {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            fragment.setDate(calendar);
        }
    }

    // RegisterFragment.OnRegisterTripListener
    @Override
    public void onTripRegistered() {
        updateTripSummary();
    }

    private void updateTripSummary() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Pre-calculate values for summary view and widget.
        new TripCalculationTask().execute(year, month);
    }

    // LogFragment.OnTripSelectedListener
    @Override
    public void onTripSelected(long id) {
        Intent intent = new Intent(this, TripDetailActivity.class).putExtra(TripDetailFragment.TRIP_ID_KEY, id);
        startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Fragment mCurrentFragment;
        private int mCurrentPosition = -1;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            // Keep a reference to the current fragment so we can communicate with it
            if(getCurrentFragment() != object)
                mCurrentFragment = (Fragment) object;

            // USe the position to check if we have handled this page already, this method gets called multiple times!
            if(position == mCurrentPosition)
                return;

            mCurrentPosition = position;

            // Do any updates that is needed for the fragment
            if (object instanceof SummaryFragment) {
                SummaryFragment fragment = (SummaryFragment) object;

                if(fragment.isResumed()){
                    fragment.updateTripData();
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case SUMMARY_PAGE:
                    return new SummaryFragment();
                case REGISTER_PAGE:
                    return new RegisterFragment();
                default:
                    return new LogFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case SUMMARY_PAGE:
                    return getString(R.string.tab_summary);
                case REGISTER_PAGE:
                    return getString(R.string.tab_register);
                case LOG_PAGE:
                    return getString(R.string.tab_log);
            }
            return null;
        }
    }

    private class TripCalculationTask extends AsyncTask<Integer, Void, Bundle> {

        @Override
        protected Bundle doInBackground(Integer... parameter) {
            if(parameter.length != 2)
                return null;

            int year = parameter[0];
            int month = parameter[1];

            // Get first day of the month
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, 1);
            String fromDate = Utility.getDateFormat().format(calendar.getTime());

            // Get last day of the month
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            String toDate = Utility.getDateFormat().format(calendar.getTime());

            /* Schematic TripProvider can´t handle this kind of selection.
            It always appends =? to the whereColumn making it impossible to use i.e. >= instead.
            Using the default content URI and adding the selection here instead.
            */
            Cursor cursor = getApplicationContext().getContentResolver().query(
                    TripProvider.Trips.CONTENT_URI,
                    null,
                    TripColumns.Date + " >= ? AND " + TripColumns.Date + " <= ?",
                    new String [] {fromDate, toDate},
                    TripColumns.Mileage + " ASC");

            cursor.moveToFirst();

            int inbound = 0;
            int outbound = 0;
            int privateMileage = 0;
            int workMileage = 0;
            int mileage = 0;
            int previousMileage = 0;

            for(int i = 0; i < cursor.getCount(); i++) {
                // Get mileage and set as inbound if first trip
                mileage = cursor.getInt(cursor.getColumnIndex(TripColumns.Mileage));
                if(i == 0){inbound = mileage;}

                // Add as work or private mileage. Only first trip can be 0.
                if(mileage > 0) {
                    if (cursor.getInt(cursor.getColumnIndex(TripColumns.TripType)) == Utility.WORK)
                        workMileage += mileage - previousMileage;
                    else
                        privateMileage += mileage - previousMileage;
                }

                previousMileage = mileage;
                cursor.moveToNext();
            }

            // Set last mileage as outbound.
            outbound = mileage;
            cursor.close();

            Bundle result = new Bundle();
            result.putString(KEY_MONTH, new SimpleDateFormat("MMMM").format(calendar.getTime()));
            result.putInt(KEY_INBOUND, inbound);
            result.putInt(KEY_OUTBOUND, outbound);
            result.putInt(KEY_PRIVATE_MILEAGE, privateMileage);
            result.putInt(KEY_WORK_MILEAGE, workMileage);

            return result;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            // Save to shared preferences for easy sharing
            if(bundle != null) {

                String period = bundle.getString(KEY_MONTH);
                period = period.substring(0,1).toUpperCase() + period.substring(1).toLowerCase();
                int inbound = bundle.getInt(KEY_INBOUND);
                int outbound = bundle.getInt(KEY_OUTBOUND);
                int privateMileage = bundle.getInt(KEY_PRIVATE_MILEAGE);
                int workMileage = bundle.getInt(KEY_WORK_MILEAGE);

                SharedPreferences prefs =  getSharedPreferences(getString(R.string.pref_summary_file_key), MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.pref_month_key), period);
                editor.putInt(getString(R.string.pref_inbound_key), inbound);
                editor.putInt(getString(R.string.pref_outbound_key), outbound);
                editor.putInt(getString(R.string.pref_total_private_key), privateMileage);
                editor.putInt(getString(R.string.pref_total_work_key), workMileage);
                editor.commit();

                Intent intent = new Intent(ACTION_WIDGET_UPDATE);
                sendBroadcast(intent);

            }
        }
    }
}
