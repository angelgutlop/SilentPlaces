package com.example.angel.silentplaces.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.angel.silentplaces.provider.PlacesContract;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import timber.log.Timber;

public class PlacesAdapter extends FlexibleAdapter<PlaceItem> {

    private Cursor mCursor;
    private Context context;
    private GeoDataClient geoDataClient;

    public PlacesAdapter(Context context) {
        super(null);
        this.context = context;

        geoDataClient = Places.getGeoDataClient(context);

    }

    private List<PlaceItem> listaPlaceItems = new ArrayList<>();
    private int placesDetected;

    public void updateDataSet(Cursor cursor) {


        if (cursor == null) return;
        this.mCursor = cursor;
        placesDetected = 0;

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor2PlaceItem(context, cursor, i);
        }

    }


    @Override
    public void updateDataSet(@Nullable List<PlaceItem> items) {
        super.updateDataSet(items);
    }

    @Override
    public void updateItem(@NonNull PlaceItem item) {
        super.updateItem(item);
    }


    private void cursor2PlaceItem(final Context context, final Cursor cursor, int i) {

        if (cursor == null) return;
        if (cursor.getCount() - 1 < i) return;

        cursor.moveToPosition(i);

        int columnaId = cursor.getColumnIndex(PlacesContract.PlaceId);
        String placeId = cursor.getString(columnaId);


        geoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {

                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place place = places.get(0);
                    PlaceItem placeItem = new PlaceItem(context, place.getName().toString(), place.getAddress().toString());
                    listaPlaceItems.add(placeItem);
                    places.release();
                } else {
                    Timber.d("Place not found.");
                }

                placesDetected++;
                if (placesDetected >= cursor.getCount()) updateDataSet(listaPlaceItems);
            }
        });


    }

}
