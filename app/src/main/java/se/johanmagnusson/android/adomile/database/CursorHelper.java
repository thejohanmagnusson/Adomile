package se.johanmagnusson.android.adomile.database;

import android.database.Cursor;
import android.support.annotation.Nullable;

import java.util.Date;

public abstract class CursorHelper {

    public static final long INVALID_ID = -1;

    public static long getTripId(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(TripColumns.ID);

        if (columnIndex >= 0 && !cursor.isNull(columnIndex)) {
            return cursor.getLong(columnIndex);
        }

        return INVALID_ID;
    }

    public static String getTripDate(Cursor cursor) {
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(TripColumns.Date);

        if (columnIndex >= 0 && !cursor.isNull(columnIndex)) {
            return cursor.getString(columnIndex);
        }
        return null;
    }

    // https://github.com/SimonVT/schematic/issues/43
    public static long getLong(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);

        if (columnIndex >= 0 && !cursor.isNull(columnIndex)) {
            return cursor.getLong(columnIndex);
        }
        return 0;
    }

    public static int getInt(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);

        if (columnIndex >= 0 && !cursor.isNull(columnIndex)) {
            return cursor.getInt(columnIndex);
        }
        return 0;
    }

    @Nullable
    public static Date getDate(Cursor cursor, String columnName) {
        long since = getLong(cursor, columnName);

        if (since > 0) {
            return new Date(since);
        }
        return null;
    }

    @Nullable
    public static String getString(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);

        if (columnIndex >= 0 && !cursor.isNull(columnIndex)) {
            return cursor.getString(columnIndex);
        }
        return null;
    }

    private CursorHelper() {
    }
}
