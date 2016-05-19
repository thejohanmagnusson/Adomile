package se.johanmagnusson.android.adomile;


import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import se.johanmagnusson.android.adomile.database.CursorHelper;
import se.johanmagnusson.android.adomile.database.TripColumns;
import se.johanmagnusson.android.adomile.database.TripProvider;
import se.johanmagnusson.android.adomile.database.TripProvider.Trips;

public class RegisterFragment extends Fragment {

    public static final String TAG = RegisterFragment.class.getName();

    private final String KEY_DATE = "date";
    private final String KEY_DESTINATION = "destination";
    private final String KEY_MILEAGE = "mileage";
    private final String KEY_NOTE = "note";
    private final String KEY_LAST_TRIP = "last_trip";

    private final Integer PRIVATE = 0;
    private final Integer WORK = 1;

    private TextView mDate;
    private EditText mDestination;
    private EditText mMileage;
    private EditText mNote;
    private Button mPrivate;
    private Button mWork;
    private ViewGroup mPreviousTripContainer;
    private View mTripCard;
    private ImageView mTripCardIcon;
    private TextView mTripCardDestination;
    private TextView mTripCardDate;
    private TextView mTripCardKm;
    private TextView mTripCardMileage;

    private long mLastTripId;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd MMM, yyyy");

    // Register trip callback
    public interface OnRegisterTripListener {
        void onTripRegistered();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        mDate = (TextView) root.findViewById(R.id.register_date);
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        mDestination = (EditText) root.findViewById(R.id.destination);
        mMileage = (EditText) root.findViewById(R.id.mileage);
        mNote = (EditText) root.findViewById(R.id.note);

        //todo: only enable buttons if valid input
        mPrivate = (Button) root.findViewById(R.id.private_button);
        mPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTrip(false);
            }
        });

        mWork = (Button) root.findViewById(R.id.work_button);
        mWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerTrip(true);
            }
        });

        mPreviousTripContainer = (ViewGroup) root.findViewById(R.id.previous_trip_container);
        mTripCard = inflater.inflate(R.layout.previous_trip_card, null);
        mTripCardIcon = (ImageView) mTripCard.findViewById(R.id.icon);
        mTripCardDestination = (TextView) mTripCard.findViewById(R.id.trip_destination);
        mTripCardDate = (TextView) mTripCard.findViewById(R.id.trip_date);
        mTripCardKm = (TextView) mTripCard.findViewById(R.id.trip_km);
        mTripCardMileage = (TextView) mTripCard.findViewById(R.id.trip_mileage);

        // Restore saved data
        if (savedInstanceState != null) {
            // Date
            if (savedInstanceState.containsKey(KEY_DATE))
                mDate.setText(savedInstanceState.getString(KEY_DATE));
            else
                setDate(Calendar.getInstance());

            // Destination
            if (savedInstanceState.containsKey(KEY_DESTINATION))
                mDestination.setText(savedInstanceState.getString(KEY_DESTINATION));

            // Mileage
            if (savedInstanceState.containsKey(KEY_MILEAGE))
                mMileage.setText(savedInstanceState.getString(KEY_MILEAGE));

            // Note
            if (savedInstanceState.containsKey(KEY_NOTE))
                mNote.setText(savedInstanceState.getString(KEY_NOTE));

            // Last trip
            if (savedInstanceState.containsKey(KEY_LAST_TRIP))
                mLastTripId = savedInstanceState.getLong(KEY_LAST_TRIP);
        }
        else
            setDate(Calendar.getInstance());

        return root;
    }

    private void showDatePicker() {
        String lastTripDate = CursorHelper.getLastTripDate(TripProvider.getLastTripDate(getContext()));
        Date date = null;

        try {
            date = mDateFormat.parse(lastTripDate);
        } catch (ParseException e) {
            Log.d(TAG, "showDatePicker, parse error: " + e.getMessage());
        }

        Bundle args = new Bundle();

        if(date != null)
            args.putLong(DatePickerFragment.KEY_MIN_DATE, date.getTime());
        else
            args.putLong(DatePickerFragment.KEY_MIN_DATE, 0L);

        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);
        datePickerFragment.show(getActivity().getSupportFragmentManager(), DatePickerFragment.TAG);
    }

    @Override
    public void onStart() {
        super.onStart();

        mLastTripId = getLastTrip();
    }

    private long getLastTrip() {
        // Get last trip (if any)
        Cursor cursor = TripProvider.getLastTrip(getContext());
        cursor.moveToFirst();

        if (cursor != null)
            return CursorHelper.getTripId(cursor);

        return CursorHelper.INVALID_ID;
    }

    public void setDate(@NonNull Calendar calendar) {
        mDate.setText(mDateFormat.format(calendar.getTime()));
    }

    private void showTripCard(long tripId) {

        // Is the card added to the view?
        if (mPreviousTripContainer.getChildCount() < 1)
            mPreviousTripContainer.addView(mTripCard);

        Cursor tripCursor = getActivity().getContentResolver().query(TripProvider.Trips.withId(tripId), null, null, null, null);

        if(tripCursor != null){
            if (tripCursor.moveToFirst()) {
                boolean isWork = CursorHelper.getInt(tripCursor, TripColumns.TripType) == WORK ? true : false;
                mTripCardIcon.setImageResource(Utility.getResourceForTripIcon(isWork));

                mTripCardDate.setText(CursorHelper.getString(tripCursor, TripColumns.Date));
                mTripCardDestination.setText(CursorHelper.getString(tripCursor, TripColumns.Destination));

                int mileage = CursorHelper.getInt(tripCursor, TripColumns.Mileage);
                mTripCardMileage.setText(String.format(this.getResources().getString(R.string.trip_mileage), mileage));

                // Get start destination for the trip to calculate distance traveled (the trip before this one)
                long previousTripId = CursorHelper.getTripId(tripCursor);

                if (previousTripId != CursorHelper.INVALID_ID) {
                    Cursor previousTripCursor = getActivity().getContentResolver().query(Trips.withId(previousTripId), null, null, null, null);

                    if(previousTripCursor != null) {
                        if (previousTripCursor.moveToFirst()) {
                            int startMileage = CursorHelper.getInt(previousTripCursor, TripColumns.Mileage);

                            mTripCardKm.setText(String.format(this.getResources().getString(R.string.trip_km), mileage - startMileage));
                        }
                    }
                }
            }
        }
    }

    private void registerTrip(boolean isWork) {
        ContentValues cv = new ContentValues();
        cv.put(TripColumns.Date, mDate.getText().toString());
        cv.put(TripColumns.Destination, mDestination.getText().toString());
        cv.put(TripColumns.Mileage, Integer.parseInt(mMileage.getText().toString()));
        cv.put(TripColumns.Note, mNote.getText().toString());
        cv.put(TripColumns.TripType, isWork ? WORK : PRIVATE);

        // The first trip registered will not have a previous trip
        if(mLastTripId != CursorHelper.INVALID_ID)
            cv.put(TripColumns.PreviousTripId, mLastTripId);

        Uri uri = getActivity().getContentResolver().insert(Trips.CONTENT_URI, cv);

        if (uri != null) {
            mLastTripId = ContentUris.parseId(uri);
            showTripCard(mLastTripId);
        }

        // Callback to activity
        //todo: probably removing this callback
        ((OnRegisterTripListener) getActivity()).onTripRegistered();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mDate != null)
            outState.putString(KEY_DATE, mDate.getText().toString());

        if (mDestination != null)
            outState.putString(KEY_DESTINATION, mDestination.getText().toString());

        if (mMileage != null)
            outState.putString(KEY_MILEAGE, mMileage.getText().toString());

        if (mNote != null)
            outState.putString(KEY_NOTE, mNote.getText().toString());

        if (mLastTripId != CursorHelper.INVALID_ID)
            outState.putLong(KEY_LAST_TRIP, mLastTripId);

        super.onSaveInstanceState(outState);
    }
}























