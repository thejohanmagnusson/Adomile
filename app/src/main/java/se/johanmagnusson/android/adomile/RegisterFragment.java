package se.johanmagnusson.android.adomile;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import se.johanmagnusson.android.adomile.database.CursorHelper;
import se.johanmagnusson.android.adomile.database.TripColumns;
import se.johanmagnusson.android.adomile.database.TripProvider;

public class RegisterFragment extends Fragment{

    public static final String TAG = RegisterFragment.class.getName();

    private final String KEY_DATE = "date";
    private final String KEY_DESTINATION = "destination";
    private final String KEY_MILEAGE = "mileage";
    private final String KEY_NOTE = "note";
    private final String KEY_PREVIOUS_TRIP = "previous_trip";

    private TextView mDate;
    private TextInputLayout mDestination;
    private TextInputLayout mMileage;
    private Button mPrivate;
    private Button mWork;
    private ViewGroup mPreviousTripContainer;
    private View mTripCard;
    private TextView mTripCardDestination;
    private TextView mTripCardDate;
    private TextView mTripCardKm;
    private TextView mTripCardMileage;

    private Uri mPreviousTrip;

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd MMM, yyyy");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        mDate = (TextView) root.findViewById(R.id.register_date);
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a date picker
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getActivity().getSupportFragmentManager(), DatePickerFragment.TAG);
            }
        });

        mDestination = (TextInputLayout) root.findViewById(R.id.destination_input_layout);
        mMileage = (TextInputLayout) root.findViewById(R.id.mileage_input_layout);

        //todo: handle add note

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
        mTripCardDestination = (TextView) mTripCard.findViewById(R.id.trip_destination);
        mTripCardDate = (TextView) mTripCard.findViewById(R.id.trip_date);
        mTripCardKm = (TextView) mTripCard.findViewById(R.id.trip_km);
        mTripCardMileage = (TextView) mTripCard.findViewById(R.id.trip_mileage);

        // Restore saved data
        if (savedInstanceState != null) {
            // Date
            if(savedInstanceState.containsKey(KEY_DATE))
                mDate.setText(savedInstanceState.getString(KEY_DATE));
            else
                setDate(Calendar.getInstance());

            // Destination
            if(savedInstanceState.containsKey(KEY_DESTINATION))
                mDestination.getEditText().setText(savedInstanceState.getString(KEY_DESTINATION));

            // Mileage
            if(savedInstanceState.containsKey(KEY_MILEAGE))
                mMileage.getEditText().setText(savedInstanceState.getString(KEY_MILEAGE));

            // Note
            //todo: add note
//            if(savedInstanceState.containsKey(KEY_NOTE))
//                mNote.getEditText().setText(savedInstanceState.getString(KEY_NOTE));

            // Previous trip
            if(savedInstanceState.containsKey(KEY_PREVIOUS_TRIP)) {
                mPreviousTrip.parse(savedInstanceState.getString(KEY_PREVIOUS_TRIP));
                showPreviousTripCard(mPreviousTrip);
            }
        }
        else {
            mDate.setText(mDateFormat.format(Calendar.getInstance().getTime()));
        }

        return root;
    }

    private void registerTrip(boolean isWork) {
        ContentValues cv = new ContentValues();
        cv.put(TripColumns.Date, mDate.getText().toString());
        Log.d(TAG, "---------- saving dte: " + mDate.getText().toString());
        cv.put(TripColumns.Destination, mDestination.getEditText().getText().toString());
        cv.put(TripColumns.Mileage, Integer.parseInt(mMileage.getEditText().getText().toString()));
        //todo: add note
        cv.put(TripColumns.Note, "Testing...");

        //todo: async?
        Uri uri = getActivity().getContentResolver().insert(TripProvider.Trips.CONTENT_URI, cv);

        if(uri != null) {
            mPreviousTrip = uri;
            showPreviousTripCard(mPreviousTrip);
        }
    }

    private void showPreviousTripCard(Uri previousTrip) {
        Log.d(TAG, "--------- Trip Card");

        // Is the card added to the view?
        if(mPreviousTripContainer.getChildCount() < 1) {
            mPreviousTripContainer.addView(mTripCard);
        }

        Cursor cursor = getActivity().getContentResolver().query(previousTrip,null, null, null, null);

        if(cursor.moveToFirst()) {
            mTripCardDate.setText(CursorHelper.getString(cursor, TripColumns.Date));
            Log.d(TAG, "---------- loading dte: " + CursorHelper.getString(cursor, TripColumns.Date));
            mTripCardDestination.setText(CursorHelper.getString(cursor, TripColumns.Destination));
            mTripCardMileage.setText(CursorHelper.getString(cursor, TripColumns.Mileage));
            //todo: add note
            //todo: set icon, work or private
        }
        else
            Log.d(TAG, "--------- Cursor empty");

        //todo: calculate km with the trip before this one. show dash (-) if no previous trip
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(mDate != null)
            outState.putString(KEY_DATE, mDate.getText().toString());

        if(mDestination != null)
            outState.putString(KEY_DESTINATION, mDestination.getEditText().getText().toString());

        if(mMileage != null)
            outState.putString(KEY_MILEAGE, mMileage.getEditText().getText().toString());

        //todo: add note
//        if(mNote != null)
//            outState.putString(KEY_NOTE, mNote.getEditText().getText().toString());

        if(mPreviousTrip != null)
            outState.putString(KEY_PREVIOUS_TRIP, mPreviousTrip.toString());

        super.onSaveInstanceState(outState);
    }

    public void setDate(@NonNull Calendar calendar) {
        mDate.setText(mDateFormat.format(calendar.getTime()));
    }
}
























