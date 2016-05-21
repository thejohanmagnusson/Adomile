package se.johanmagnusson.android.adomile;


import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import se.johanmagnusson.android.adomile.database.TripColumns;
import se.johanmagnusson.android.adomile.database.TripProvider;

public class SummaryFragment extends Fragment{

    public static final String TAG = SummaryFragment.class.getName();

    private static final String KEY_FROM_DATE = "from_date";
    private static final String KEY_TO_DATE = "to_date";
    private static final String KEY_INBOUND = "inbound";
    private static final String KEY_OUTBOUND = "outbound";
    private static final String KEY_PRIVATE_MILEAGE = "private_mileage";
    private static final String KEY_WORK_MILEAGE = "work_mileage";

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

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get first day of the month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String fromDate = Utility.getDateFormat().format(calendar.getTime());

        // Get last day of the month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String toDate = Utility.getDateFormat().format(calendar.getTime());

        new TripCalculationTask().execute(fromDate, toDate);
    }

    public class TripCalculationTask extends AsyncTask<String, Void, Bundle> {

        @Override
        protected Bundle doInBackground(String... parameter) {
            if(parameter.length == 0)
                return null;

            String fromDate = parameter[0];
            String toDate = parameter[1];

            /* Schematic TripProvider can´t handle this kind of selection.
            It always appends =? to the whereColumn making it impossible to use i.e. >= instead.
            Using the default content URI and adding the selection here instead.
            */
            Cursor cursor = getActivity().getContentResolver().query(
                    TripProvider.Trips.CONTENT_URI,
                    null,
                    TripColumns.Date + " >= ? AND " + TripColumns.Date + " <= ?",
                    new String [] {fromDate, toDate},
                    TripColumns.Mileage + " ASC");

            cursor.moveToFirst();

            int inbound = 0;
            int outbound = 0;
            int privateMileage = 0;
            int workMileage = 0;
            int mileage = 0;
            int previousMileage = 0;

            for(int i = 0; i < cursor.getCount(); i++) {
                // Get mileage and set as inbound if first trip
                mileage = cursor.getInt(cursor.getColumnIndex(TripColumns.Mileage));
                if(i == 0){inbound = mileage;}

                // Add as work or private mileage
                if(cursor.getInt(cursor.getColumnIndex(TripColumns.TripType)) == Utility.WORK)
                    workMileage += mileage - previousMileage;
                else
                    privateMileage += mileage - previousMileage;

                previousMileage = mileage;
                cursor.moveToNext();
            }

            // Set last mileage as outbound.
            outbound = mileage;
            cursor.close();

            Bundle result = new Bundle();
            result.putString(KEY_FROM_DATE, fromDate);
            result.putString(KEY_TO_DATE, toDate);
            result.putInt(KEY_INBOUND, inbound);
            result.putInt(KEY_OUTBOUND, outbound);
            result.putInt(KEY_PRIVATE_MILEAGE, privateMileage);
            result.putInt(KEY_WORK_MILEAGE, workMileage);

            return result;
        }

        @Override
        protected void onPostExecute(Bundle bundle) {
            if(bundle != null) {

                mPeriod.setText(bundle.getString(KEY_FROM_DATE) + " - " + bundle.getString(KEY_TO_DATE));
                mInbound.setText(Integer.toString(bundle.getInt(KEY_INBOUND)));
                mOutbound.setText(Integer.toString(bundle.getInt(KEY_OUTBOUND)));

                int privateMileage = bundle.getInt(KEY_PRIVATE_MILEAGE);
                mTotalPrivate.setText(Integer.toString(privateMileage));

                int workMileage = bundle.getInt(KEY_WORK_MILEAGE);
                mTotalWork.setText(Integer.toString(workMileage));

                updateChart(privateMileage, workMileage);
            }
        }
    }

    //todo: handle if no data for the period, show some icon instead of chart?
    private void updateChart(int privateMileage, int workMileage) {
        List<SliceValue> values = new ArrayList<>();

        SliceValue workValue = new SliceValue((float) workMileage * 30 + 15, ChartUtils.pickColor());
        SliceValue privateValue = new SliceValue((float) privateMileage * 30 + 15, ChartUtils.pickColor());
        values.add(workValue);
        values.add(privateValue);

        mChartData = new PieChartData(values);
        mChartData.setHasCenterCircle(true);

        mChart.setPieChartData(mChartData);
    }
}






















