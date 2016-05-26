package se.johanmagnusson.android.adomile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener,
                   LogFragment.OnTripSelectedListener,
                   RegisterFragment.OnRegisterTripListener {

    public static final String TAG = MainActivity.class.getName();

    public static final int SUMMARY_PAGE = 0;
    public static final int REGISTER_PAGE = 1;
    public static final int LOG_PAGE = 2;
    public static final String KEY_SELECTED_TAB = "selected_tab";

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
        // Set flag so other fragments can update if needed
    }

    // LogFragment.OnTripSelectedListener
    @Override
    public void onTripSelected(long id) {
        //todo: check/handle if two pane view

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

            // USe the position to check if we have handled this page already, this method getts called multiple times!
            if(position == mCurrentPosition)
                return;

            mCurrentPosition = position;

            // Do any updates that is needed for the fragment
            if (object instanceof SummaryFragment) {
                SummaryFragment fragment = (SummaryFragment) object;

                if(fragment.isResumed()){
                    fragment.upateTripData();
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case SUMMARY_PAGE:
                    Log.d(TAG, "------------- Get Summary fragment");
                    return new SummaryFragment();
                case REGISTER_PAGE:
                    Log.d(TAG, "------------- Get Register fragment");
                    return new RegisterFragment();
                default:
                    Log.d(TAG, "------------- Get Log fragment");
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
}
