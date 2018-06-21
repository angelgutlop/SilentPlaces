package com.example.angel.silentplaces.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.example.angel.silentplaces.provider.PlacesContract;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class PlacesUtils {

    public interface placesDecodeTask {
        public void onPlacesReady(List<Place> places);
    }

    public static void PlacesDecoce(final Context context, Cursor cursor, final placesDecodeTask placesDecodeTask) {

        if (cursor == null) return;
        if (cursor.getCount() < 1) return;
        
        GeoDataClient geoDataClient = Places.getGeoDataClient(context);

        int columnaId = cursor.getColumnIndex(PlacesContract.PlaceId);

        List<String> placeIds = new ArrayList<>();
        final List<Place> listaPlaces = new ArrayList<>();

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            placeIds.add(cursor.getString(columnaId));
        }

        Task<PlaceBufferResponse> tarea = geoDataClient.getPlaceById(placeIds.toArray(new String[placeIds.size()]));

        tarea.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                PlaceBufferResponse placeBufferResponse = task.getResult();
                for (Place place : placeBufferResponse) {
                    listaPlaces.add(place);
                }
                placesDecodeTask.onPlacesReady(listaPlaces);
            }
        });


    }
}
