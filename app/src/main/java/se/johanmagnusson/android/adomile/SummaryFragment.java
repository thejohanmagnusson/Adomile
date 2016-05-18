package se.johanmagnusson.android.adomile;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import se.johanmagnusson.android.adomile.database.TripColumns;
import se.johanmagnusson.android.adomile.database.TripProvider;

public class SummaryFragment extends Fragment{

    public static final String TAG = SummaryFragment.class.getName();

    private PieChartView mChart;
    private PieChartData mChartData;

    private TextView mTotalPrivate;
    private TextView mTotalWork;
    private TextView mPeriod;
    private TextView mInbound;
    private TextView mOutbound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_summary, container, false);

        mChart = (PieChartView) root.findViewById(R.id.chart);
        mTotalPrivate = (TextView) root.findViewById(R.id.total_private_mileage);
        mTotalWork = (TextView) root.findViewById(R.id.total_work_mileage);
        mPeriod = (TextView) root.findViewById(R.id.period);
        mInbound = (TextView) root.findViewById(R.id.inbound_km);
        mOutbound = (TextView) root.findViewById(R.id.outbound_km);

        mChart.setChartRotationEnabled(false);
        mChart.setClickable(false);

        /* Schematic TripProvider can´t handle this kind of selection.
           It always appends =? to the whereColumn making it impossible to use i.e. >= instead.
           Using the default content URI and adding the selection here instead.
         */
        Cursor cursor = getActivity().getContentResolver().query(
                TripProvider.Trips.CONTENT_URI,
                null,
                TripColumns.Date + " >= ? AND " + TripColumns.Date + " <= ?",
                new String [] {"2016 05 14", "2016 05 19"},
                TripColumns.Mileage + " DESC");

//        if(cursor != null) {
//            Log.d(TAG, "-------- Cursor ok");
//            Log.d(TAG, "-------- Cursor count: " + cursor.getCount());
//        }
//        else
//            Log.d(TAG, "-------- Cursor NULL");

        updateChart();

        return root;
    }

    //todo: get data from db, use asyncTask to fetch and calculate
    //todo: handle if no data for the period, show some icon instead of chart?
    private void updateChart() {
        int numValues = 2;

        List<SliceValue> values = new ArrayList<SliceValue>();

        for (int i = 0; i < numValues; ++i) {
            SliceValue sliceValue = new SliceValue((float) Math.random() * 30 + 15, ChartUtils.pickColor());
            values.add(sliceValue);
        }

        mChartData = new PieChartData(values);
        mChartData.setHasCenterCircle(true);

        mChart.setPieChartData(mChartData);
    }
}
