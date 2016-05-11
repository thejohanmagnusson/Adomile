package se.johanmagnusson.android.adomile;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegisterFragment extends Fragment{

    public static final String TAG = RegisterFragment.class.getName();

    private final String KEY_DATE = "date";

    private TextView mDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");

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

        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_DATE))
            mDate.setText(savedInstanceState.getString(KEY_DATE));
        else
            setDate(Calendar.getInstance());

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_DATE, mDate.getText().toString());

        super.onSaveInstanceState(outState);
    }

    public void setDate(@NonNull Calendar calendar) {
        mDate.setText(dateFormat.format(calendar.getTime()));
    }
}
























