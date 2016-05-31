package se.johanmagnusson.android.adomile;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import se.johanmagnusson.android.adomile.database.TripColumns;
import se.johanmagnusson.android.adomile.database.TripProvider.Trips;
import se.johanmagnusson.android.adomile.database.generated.TripProvider;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
        Checks to make sure that the content provider is registered correctly.
     */
    public void testProviderRegistration() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), TripProvider.class.getName());

        try {
            // Throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Registered authority should match the authority from our provider.
            assertEquals("Error: TripProvider registration: " + providerInfo.authority + " should be : " + TripProvider.AUTHORITY,
                    providerInfo.authority, TripProvider.AUTHORITY);
        }
        catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: TripProvider not registered at " + mContext.getPackageName(), false);
        }
    }


    public void testTripQuery() {

        Context context = getContext();

        ContentValues cv = new ContentValues();
        cv.put(TripColumns.Date, "YYYY-MM-DD HH:MM:SS");
        cv.put(TripColumns.Destination, "Kemicentrum");
        cv.put(TripColumns.Mileage, 12345);
        cv.put(TripColumns.Note, "Testing...");

        Uri uri = context.getContentResolver().insert(Trips.CONTENT_URI, cv);

        assertNotNull("Insertion failed", uri);

        Cursor tripCursor = context.getContentResolver().query(
                Trips.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("No trip entries, empty cursor returned. ", tripCursor.moveToFirst());
        tripCursor.close();

        int countDeleted = context.getContentResolver().delete(Trips.CONTENT_URI, null, null);

        assertTrue("No trips deleted.", countDeleted > 0);

    }
}





















