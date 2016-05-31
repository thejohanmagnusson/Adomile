package se.johanmagnusson.android.adomile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.text.SimpleDateFormat;

public class Utility {

    // Trip types
    public static final Integer PRIVATE = 0;
    public static final Integer WORK = 1;

    public static int getResourceForTripColor(boolean isWork) {
        return isWork ? R.color.colorAccentAlternative : R.color.colorAccent;
    }

    public static Drawable getDrawable(Context context, int id) {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            return context.getResources().getDrawable(id);
        }
        else {
            return context.getResources().getDrawable(id, null);
        }
    }

    public static SimpleDateFormat getDateFormat() {return new SimpleDateFormat("dd MMM, yyyy");}
}
