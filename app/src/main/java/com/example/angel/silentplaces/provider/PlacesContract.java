package com.example.angel.silentplaces.provider;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.Unique;

import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public class PlacesContract {

    @DataType(TEXT) @NotNull
    @Unique
    public static final String PlaceId = "placeID";

    @DataType(TEXT) @NotNull
    public static final String PlaceName= "name";

    @DataType(TEXT) @NotNull
    public static final String PlaceAddres ="address";

}
