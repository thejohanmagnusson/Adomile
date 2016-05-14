package se.johanmagnusson.android.adomile;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private final String KEY_TRIP_TYPE = "trip_type";
    private final String KEY_PREVIOUS_TRIP = "previous_trip";

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
        mTripCardIcon  = (ImageView) mTripCard.findViewById(R.id.icon);
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
                mDestination.setText(savedInstanceState.getString(KEY_DESTINATION));

            // Mileage
            if(savedInstanceState.containsKey(KEY_MILEAGE))
                mMileage.setText(savedInstanceState.getString(KEY_MILEAGE));

            // Note
            if(savedInstanceState.containsKey(KEY_NOTE))
                mNote.setText(savedInstanceState.getString(KEY_NOTE));

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
        cv.put(TripColumns.Destination, mDestination.getText().toString());
        cv.put(TripColumns.Mileage, Integer.parseInt(mMileage.getText().toString()));
        cv.put(TripColumns.Note, mNote.getText().toString());
        cv.put(KEY_TRIP_TYPE, isWork ? WORK : PRIVATE);

        //todo: async?
        Uri uri = getActivity().getContentResolver().insert(TripProvider.Trips.CONTENT_URI, cv);

        if(uri != null) {
            mPreviousTrip = uri;
            showPreviousTripCard(mPreviousTrip);
        }
    }

    private void showPreviousTripCard(Uri previousTrip) {

        // Is the card added to the view?
        if(mPreviousTripContainer.getChildCount() < 1) {
            mPreviousTripContainer.addView(mTripCard);
        }

        //todo: async?
        Cursor cursor = getActivity().getContentResolver().query(previousTrip, null, null, null, null);

        if(cursor.moveToFirst()) {
            //todo: format date to "today" if current date
            mTripCardDate.setText(CursorHelper.getString(cursor, TripColumns.Date));
            mTripCardDestination.setText(CursorHelper.getString(cursor, TripColumns.Destination));
            mTripCardMileage.setText(CursorHelper.getString(cursor, TripColumns.Mileage));

            boolean isWork = CursorHelper.getInt(cursor, TripColumns.TripType) == WORK ? true : false;
            mTripCardIcon.setImageResource( isWork ? R.mipmap.ic_work_black_48dp : R.mipmap.ic_account_circle_black_48dp);
        }

        //todo: calculate km with the trip before this one. show dash (-) if no previous trip
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(mDate != null)
            outState.putString(KEY_DATE, mDate.getText().toString());

        if(mDestination != null)
            outState.putString(KEY_DESTINATION, mDestination.getText().toString());

        if(mMileage != null)
            outState.putString(KEY_MILEAGE, mMileage.getText().toString());

        if(mNote != null)
            outState.putString(KEY_NOTE, mNote.getText().toString());

        if(mPreviousTrip != null)
            outState.putString(KEY_PREVIOUS_TRIP, mPreviousTrip.toString());

        super.onSaveInstanceState(outState);
    }

    public void setDate(@NonNull Calendar calendar) {
        mDate.setText(mDateFormat.format(calendar.getTime()));
    }
}
























