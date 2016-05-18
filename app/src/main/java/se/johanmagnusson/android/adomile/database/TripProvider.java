package se.johanmagnusson.android.adomile.database;

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
                defaultSort = TripColumns.Mileage + " DESC")
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
}



























