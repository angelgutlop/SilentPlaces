package com.example.angel.silentplaces.provider;

import android.content.Context;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.NotifyBulkInsert;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.NotifyUpdate;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = PlacesProvider.AUTHORITY, database = PlacesDataBase.class)

public class PlacesProvider {
    public static final String AUTHORITY = "com.example.angel.silentplaces";
    public static final String pathPLACES = "places";
    public static final String pathPLACESItem = pathPLACES + "/*";


    @TableEndpoint(table = PlacesDataBase.PLACES)
    public static class PlacesTable {

        @ContentUri(
                path = pathPLACES,
                type = "vnd.android.cursor.dir/list",
                defaultSort = PlacesContract.PlaceId + " DESC")
        public static final Uri CONTENT_URI_PLACES = Uri.parse("content://" + AUTHORITY + "/" + pathPLACES);


        @InexactContentUri(
                path = pathPLACESItem,
                name = "LIST_ID",
                type = "vnd.android.cursor.item",
                whereColumn = PlacesContract.PlaceId,
                pathSegment = 1)
        public static Uri withPlace(String place) {
            return CONTENT_URI_PLACES.buildUpon().appendEncodedPath(place).build();
        }


        @NotifyBulkInsert
        public static Uri[] onBulkInsert(Uri uri, String where, String[] whereArgs) {
            return new Uri[]{PlacesTable.CONTENT_URI_PLACES};
        }

        @NotifyInsert
        public static Uri[] onInsert(Uri uri, String where, String[] whereArgs) {
            return new Uri[]{PlacesTable.CONTENT_URI_PLACES};
        }

        @NotifyUpdate(paths = pathPLACESItem)
        public static Uri[] onUpdate(Context context, Uri uri, String where, String[] whereArgs) {
            final String placeKey = uri.getPathSegments().get(1);
            return new Uri[]{PlacesTable.CONTENT_URI_PLACES, withPlace(placeKey)};
        }


        @NotifyDelete
        public static Uri[] onDelete(Context context, Uri uri, String where, String[] whereArgs) {
            final String placeKey = uri.getPathSegments().get(1);
            return new Uri[]{PlacesTable.CONTENT_URI_PLACES, withPlace(placeKey)};
        }


    }

}
