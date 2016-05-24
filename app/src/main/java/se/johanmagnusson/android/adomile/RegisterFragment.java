package se.johanmagnusson.android.adomile;


import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

    private RelativeLayout mContainer;
    private TextView mDate;
    private EditText mDestination;
    private EditText mMileage;
    private EditText mNote;
    private Button mPrivate;
    private Button mWork;
    private CardView mTripCard;
    private View mTripCardIconShape;
    private TextView mTripCardIconText;
    private TextView mTripCardDestination;
    private TextView mTripCardDate;
    private TextView mTripCardKm;
    private TextView mTripCardMileage;

    private long mLastTripId;

    private SimpleDateFormat mDateFormat = Utility.getDateFormat();

    // Register trip callback
    public interface OnRegisterTripListener {
        void onTripRegistered();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        mContainer = (RelativeLayout) root.findViewById(R.id.container);

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
                if(validateInput())
                    registerTrip(false);
            }
        });

        mWork = (Button) root.findViewById(R.id.work_button);
        mWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput())
                    registerTrip(true);
            }
        });

        mTripCard = (CardView) root.findViewById(R.id.trip_card);
        mTripCard.setVisibility(View.INVISIBLE);
        mTripCardIconShape = (View) root.findViewById(R.id.icon_shape);
        mTripCardIconText = (TextView) root.findViewById(R.id.icon_text);
        mTripCardDestination = (TextView) root.findViewById(R.id.trip_destination);
        mTripCardDate = (TextView) root.findViewById(R.id.trip_date);
        mTripCardKm = (TextView) root.findViewById(R.id.trip_km);
        mTripCardMileage = (TextView) root.findViewById(R.id.trip_mileage);

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
        Cursor cursor = TripProvider.getLastTrip(getContext());
        Date date = null;

        if(cursor.getCount() > 0){
            String lastTripDate = CursorHelper.getTripDate(cursor);

            try {
                date = mDateFormat.parse(lastTripDate);
            } catch (ParseException e) {
                Log.d(TAG, "showDatePicker, parse error: " + e.getMessage());
            }
            finally {
                cursor.close();
            }
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

        Log.d(TAG, "------- start");

        mLastTripId = getLastTrip();
        updateTripCard(mLastTripId);
    }

    private long getLastTrip() {
        // Get last trip (if any)
        Cursor cursor = TripProvider.getLastTrip(getContext());

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            long id = CursorHelper.getTripId(cursor);
            cursor.close();
            return id;
        }
        return CursorHelper.INVALID_ID;
    }

    public void setDate(@NonNull Calendar calendar) {
        String date = mDateFormat.format(calendar.getTime());
        mDate.setText(date);
        mDate.setContentDescription(date);
    }

    //todo: make method
    private boolean validateInput() {
        boolean valid = true;

        return valid;
    }

    private void registerTrip(boolean isWork) {
        ContentValues cv = new ContentValues();
        cv.put(TripColumns.Date, mDate.getText().toString());
        cv.put(TripColumns.Destination, mDestination.getText().toString());
        cv.put(TripColumns.Mileage, Integer.parseInt(mMileage.getText().toString()));
        cv.put(TripColumns.Note, mNote.getText().toString());
        cv.put(TripColumns.TripType, isWork ? Utility.WORK : Utility.PRIVATE);

        // The first trip registered will not have a previous trip
        if(mLastTripId != CursorHelper.INVALID_ID)
            cv.put(TripColumns.PreviousTripId, mLastTripId);

        Uri uri = getActivity().getContentResolver().insert(Trips.CONTENT_URI, cv);

        if (uri != null) {
            mLastTripId = ContentUris.parseId(uri);
            updateTripCard(mLastTripId);
        }

        // Callback to activity
        //todo: probably removing this callback
        ((OnRegisterTripListener) getActivity()).onTripRegistered();
    }

    private void updateTripCard(long tripId) {

        Cursor tripCursor = getActivity().getContentResolver().query(TripProvider.Trips.withId(tripId), null, null, null, null);

        if(tripCursor != null && tripCursor.moveToFirst()){
            boolean isWork = CursorHelper.getInt(tripCursor, TripColumns.TripType) == Utility.WORK ? true : false;

            Drawable icon;
            String letter;

            // FIXME: 2016-05-23 handel dep, api
            if(isWork) {
                icon = getContext().getResources().getDrawable(R.drawable.circle_work);
                letter =getContext().getResources().getString(R.string.trip_type_letter_work);
            }
            else {
                icon = getContext().getResources().getDrawable(R.drawable.circle_private);
                letter = getContext().getResources().getString(R.string.trip_type_letter_private);
            }
            mTripCardIconShape.setBackground(icon);
            mTripCardIconText.setText(letter);
            //todo: content description icon

            String destination = tripCursor.getString(tripCursor.getColumnIndex(TripColumns.Destination));
            mTripCardDestination.setText(destination);
            mTripCardDestination.setContentDescription(destination);

            String date = tripCursor.getString(tripCursor.getColumnIndex(TripColumns.Date));
            mTripCardDate.setText(date);
            mTripCardDate.setContentDescription(date);

            int mileage = tripCursor.getInt(tripCursor.getColumnIndex(TripColumns.Mileage));
            String formattedMileage = String.format(getContext().getResources().getString(R.string.trip_mileage), mileage);
            mTripCardMileage.setText(formattedMileage);
            mTripCardMileage.setContentDescription(formattedMileage);

            // Get start destination for the trip to calculate distance traveled (the trip before this one)
            long previousTripId = CursorHelper.getTripId(tripCursor);

            if (previousTripId != CursorHelper.INVALID_ID) {
                Cursor previousTripCursor = getActivity().getContentResolver().query(Trips.withId(previousTripId), null, null, null, null);

                if(previousTripCursor != null && previousTripCursor.moveToFirst()) {
                    int startMileage = tripCursor.getInt(tripCursor.getColumnIndex(TripColumns.Mileage));

                    String formattedStartMileage = String.format(getContext().getResources().getString(R.string.trip_mileage_summary), mileage - startMileage);
                    mTripCardKm.setText(formattedStartMileage);
                }
            }
            tripCursor.close();

            if(mTripCard.getVisibility() != View.VISIBLE)
                mTripCard.setVisibility(View.VISIBLE);

            // Animate card
            mTripCard.animate().translationX(mContainer.getWidth()).setDuration(250).withLayer();

        }
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























