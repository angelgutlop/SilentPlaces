package com.example.angel.silentplaces.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = PlacesDataBase.VERSION, fileName = "placesDB")

public class PlacesDataBase {

    public static final int VERSION = 1;

    @Table(PlacesContract.class)
    public static final String PLACES = "places";
}
