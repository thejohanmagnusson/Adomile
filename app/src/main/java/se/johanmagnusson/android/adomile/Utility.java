package se.johanmagnusson.android.adomile;

import java.text.SimpleDateFormat;

public class Utility {

    public static int getResourceForTripIcon(boolean isWork) {
        return isWork ? R.mipmap.ic_work_black_48dp : R.mipmap.ic_account_circle_black_48dp;
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("dd MMM, yyyy");
    }
}
