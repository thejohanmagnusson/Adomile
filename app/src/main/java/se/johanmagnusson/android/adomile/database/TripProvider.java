package se.johanmagnusson.android.adomile.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = TripProvider.AUTHORITY, database = TripDatabase.class)

public final class TripProvider {

    public static final String AUTHORITY = "se.johanmagnusson.android.adomile.TripProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String TRIPS = "trips";
    }

    interface Type {
        String TRIP = "vnd.android.cursor.dir/trip";
    }

    interface Sort {
        String DESC = " DESC ";
        String ASC = " ASC ";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();

        for (String path : paths) {
            builder.appendPath(path);
        }

        return builder.build();
    }

    @TableEndpoint(table = TripDatabase.TRIPS) public static class Trips {

        // Get all trips
        @ContentUri(
                path = Path.TRIPS,
                type = Type.TRIP,
                defaultSort = TripColumns.Mileage + Sort.DESC)
        public static final Uri CONTENT_URI = buildUri(Path.TRIPS);

        // Get trip by it´s id
        @InexactContentUri(
                name = "TRIP_ID",
                path = Path.TRIPS + "/#",
                type = Type.TRIP,
                whereColumn = TripColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.TRIPS, String.valueOf(id));
        }
    }

    public static Cursor getLastTrip(Context context) {
        Cursor cursor = context.getContentResolver().query(
                Trips.CONTENT_URI,
                null,
                null,
                null,
                TripColumns.Mileage + Sort.DESC + "LIMIT 1");

        return cursor;
    }
}



























