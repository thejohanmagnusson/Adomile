package se.johanmagnusson.android.adomile;


import android.content.Context;
import android.content.SharedPreferences;
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
import lecho.lib.hellocharts.view.PieChartView;

public class SummaryFragment extends Fragment{

    public static final String TAG = SummaryFragment.class.getName();

    private PieChartView mChart;

    private TextView mTotalPrivate;
    private TextView mTotalWork;
    private TextView mPeriod;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_summary, container, false);

        mChart = (PieChartView) root.findViewById(R.id.chart);
        mTotalPrivate = (TextView) root.findViewById(R.id.total_private_mileage);
        mTotalWork = (TextView) root.findViewById(R.id.total_work_mileage);
        mPeriod = (TextView) root.findViewById(R.id.period);

        mChart.setChartRotationEnabled(false);
        mChart.setClickable(false);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Also called form the activity since fragment lifecycle is also depends on the page adapter
        updateTripData();
    }

    public void updateTripData() {
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.pref_summary_file_key), Context.MODE_PRIVATE);

        String period = prefs.getString(getString(R.string.pref_month_key), "");
        mPeriod.setText(period);
        mPeriod.setContentDescription(period);

        int inbound = prefs.getInt(getString(R.string.pref_inbound_key), 0);
        int outbound = prefs.getInt(getString(R.string.pref_outbound_key), 0);
        int privateMileage = prefs.getInt(getString(R.string.pref_total_private_key), 0);
        int workMileage = prefs.getInt(getString(R.string.pref_total_work_key), 0);

        updateChart(inbound, outbound, privateMileage, workMileage);

        String sPrivateMileage = String.format(getContext().getResources().getString(R.string.trip_mileage_summary), privateMileage);
        mTotalPrivate.setText(sPrivateMileage);
        mTotalPrivate.setContentDescription(sPrivateMileage);

        String sWorkMileage = String.format(getContext().getResources().getString(R.string.trip_mileage_summary), workMileage);
        mTotalWork.setText(sWorkMileage);
        mTotalWork.setContentDescription(sWorkMileage);
    }



    private void updateChart(int inbound, int outbound, int privateMileage, int workMileage) {
        List<SliceValue> values = new ArrayList<>();

        SliceValue privateValue = new SliceValue((float) privateMileage * 30 + 15, getContext().getResources().getColor(Utility.getResourceForTripColor(false)));
        SliceValue workValue = new SliceValue((float) workMileage * 30 + 15, getContext().getResources().getColor(Utility.getResourceForTripColor(true)));

        values.add(privateValue);
        values.add(workValue);

        PieChartData mChartData = new PieChartData(values);
        mChartData.setHasCenterCircle(true);

        mChartData.setCenterText1FontSize(20);
        mChartData.setCenterText1(getContext().getResources().getString(R.string.mileage));

        String mileage = String.format(getContext().getResources().getString(R.string.in_out_mileage), inbound, outbound);
        mChartData.setCenterText2(mileage);
        mChartData.setCenterText2FontSize(20);

        mChart.setPieChartData(mChartData);
        mChart.setContentDescription(mileage);
    }
}






















