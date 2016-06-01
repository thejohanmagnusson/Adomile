package se.johanmagnusson.android.adomile;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import se.johanmagnusson.android.adomile.database.CursorHelper;
import se.johanmagnusson.android.adomile.database.TripColumns;
import se.johanmagnusson.android.adomile.database.TripProvider;


public class TripDetailFragment extends Fragment {

    public static final String TRIP_ID_KEY = "trip_id";
    public static final String TRIP_TYPE_KEY = "trip_type";
    public static final String DATE_KEY = "date";
    public static final String DESTINATION_KEY = "destination";
    public static final String MILEAGE_KEY = "mileage";
    public static final String NOTE_KEY = "note";

    private boolean mIsWork;

    private View mIconShape;
    private TextView mIconText;
    private TextView mDate;
    private TextView mDestination;
    private TextView mMileage;
    private TextView mNote;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);

        mIconShape = root.findViewById(R.id.icon_shape);
        mIconText = (TextView) root.findViewById(R.id.icon_text);
        mDate = (TextView) root.findViewById(R.id.date);
        mDestination = (TextView) root.findViewById(R.id.destination);
        mMileage = (TextView) root.findViewById(R.id.mileage);
        mNote = (TextView) root.findViewById(R.id.note);

        if(savedInstanceState == null) {
            Bundle args = getArguments();

            if(args != null && args.containsKey(TRIP_ID_KEY)) {
                long id = args.getLong(TRIP_ID_KEY);

                Cursor cursor = getActivity().getContentResolver().query(TripProvider.Trips.withId(id), null, null, null, null);

                if(cursor != null && cursor.moveToFirst()) {
                    mIsWork = CursorHelper.getInt(cursor, TripColumns.TripType) == Utility.WORK;

                    Drawable icon;
                    String letter;

                    if(mIsWork) {
                        icon = Utility.getDrawable(getContext(), R.drawable.circle_work);
                        letter =getContext().getResources().getString(R.string.trip_type_letter_work);
                    }
                    else {
                        icon = Utility.getDrawable(getContext(), R.drawable.circle_private);
                        letter = getContext().getResources().getString(R.string.trip_type_letter_private);
                    }

                    mIconShape.setBackground(icon);
                    mIconText.setText(letter);

                    String destination = cursor.getString(cursor.getColumnIndex(TripColumns.Destination));
                    mDestination.setText(destination);
                    mDestination.setContentDescription(destination);

                    String date = cursor.getString(cursor.getColumnIndex(TripColumns.Date));
                    mDate.setText(date);
                    mDate.setContentDescription(date);

                    int mileage = cursor.getInt(cursor.getColumnIndex(TripColumns.Mileage));
                    String formattedMileage = String.format(getContext().getResources().getString(R.string.trip_mileage), mileage);
                    mMileage.setText(formattedMileage);
                    mMileage.setContentDescription(formattedMileage);

                    cursor.close();
                }
            }
        }
        else {
            mIsWork = savedInstanceState.getBoolean(TRIP_TYPE_KEY);

            Drawable icon = mIsWork ? Utility.getDrawable(getActivity(), R.drawable.circle_work) : Utility.getDrawable(getActivity(), R.drawable.circle_private);
            mIconShape.setBackground(icon);

            int iconTextId = mIsWork ? R.string.trip_type_letter_work : R.string.trip_type_letter_private;
            mIconText.setText(getActivity().getResources().getString(iconTextId));

            int iconDescId = mIsWork ? R.string.trip_type_work : R.string.trip_type_private;
            mIconText.setContentDescription(getActivity().getResources().getString(iconDescId));

            mDate.setText(savedInstanceState.getString(DATE_KEY));
            mDestination.setText(savedInstanceState.getString(DESTINATION_KEY));
            mMileage.setText(savedInstanceState.getString(MILEAGE_KEY));
            mNote.setText(savedInstanceState.getString(NOTE_KEY));
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TRIP_TYPE_KEY, mIsWork);
        outState.putString(DATE_KEY, mDate.getText().toString());
        outState.putString(DESTINATION_KEY, mDestination.getText().toString());
        outState.putString(MILEAGE_KEY, mMileage.getText().toString());
        outState.putString(NOTE_KEY, mNote.getText().toString());

        super.onSaveInstanceState(outState);
    }
}
