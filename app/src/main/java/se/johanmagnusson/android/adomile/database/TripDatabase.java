package se.johanmagnusson.android.adomile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

@Database(version = TripDatabase.VERSION)
public final class TripDatabase {

    public static final int VERSION = 1;

    @Table(TripColumns.class) public static final String TRIPS = "trips";

    @OnCreate public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @OnConfigure public static void onConfigure(SQLiteDatabase db) {
    }

    @ExecOnCreate public static final String EXEC_ON_CREATE = "SELECT * FROM " + TRIPS;
}
