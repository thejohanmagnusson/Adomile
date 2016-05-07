package se.johanmagnusson.android.adomile.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface TripColumns {

    @DataType(INTEGER) @PrimaryKey @AutoIncrement String ID = "_id";

    @DataType(TEXT) @NotNull String Date = "date";
    @DataType(TEXT) @NotNull String Destination = "destination";
    @DataType(INTEGER) @NotNull String Mileage = "mileage";
    @DataType(TEXT) String Notes = "notes";

}
