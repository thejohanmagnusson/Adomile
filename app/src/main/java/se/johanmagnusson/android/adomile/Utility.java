package se.johanmagnusson.android.adomile;

import android.content.Context;

import java.text.SimpleDateFormat;

public class Utility {

    // Trip types
    public static final Integer PRIVATE = 0;
    public static final Integer WORK = 1;

    public static int getResourceForTripIcon(boolean isWork) {
        return isWork ? R.mipmap.ic_work_black_48dp : R.mipmap.ic_account_circle_black_48dp;
    }

    public static int getResourceForTripColor(boolean isWork) {
        return isWork ? R.color.colorAccentAlternative : R.color.colorAccent;
    }

    public static String getContentDescriptionForTripIcon(Context context, boolean isWork) {
        return context.getResources().getString(isWork ? R.string.trip_type_work : R.string.trip_type_private);
    }

    public static SimpleDateFormat getDateFormat() {return new SimpleDateFormat("dd MMM, yyyy");}
}
