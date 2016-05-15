package se.johanmagnusson.android.adomile;

public class Utility {

    public static int getResourceForTripIcon(boolean isWork) {
        return isWork ? R.mipmap.ic_work_black_48dp : R.mipmap.ic_account_circle_black_48dp;
    }

    //todo: fix utility method
//    public static String getFormatedDate() {
//
//    }
}
