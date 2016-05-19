package se.johanmagnusson.android.adomile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public static final String TAG = DatePickerFragment.class.getName();

    public static final String KEY_MIN_DATE = "min_date";

    private OnDateSetListener mOnDateSetListener;
    private long mMinDate;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //check so activity implements the listener
        try {
            mOnDateSetListener = (OnDateSetListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DatePickerListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use current date as default
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set minimum allowed date
        if(savedInstanceState == null) {
            Bundle args = getArguments();

            if(args != null && args.containsKey(KEY_MIN_DATE))
                mMinDate = args.getLong(KEY_MIN_DATE);
        }
        else
            mMinDate = savedInstanceState.getLong(KEY_MIN_DATE);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), mOnDateSetListener, year, month, day);
        dialog.getDatePicker().setMinDate(mMinDate);

        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_MIN_DATE, mMinDate);

        super.onSaveInstanceState(outState);
    }
}
