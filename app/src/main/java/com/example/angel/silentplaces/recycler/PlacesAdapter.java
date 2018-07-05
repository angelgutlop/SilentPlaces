package com.example.angel.silentplaces.recycler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class PlacesAdapter extends FlexibleAdapter<PlaceItem> {

    private Context context;

    public PlacesAdapter(Context context) {
        super(null);
        this.context = context;
    }


    public void updatePlaces(@Nullable List<Place> places) {
        List<PlaceItem> items = new ArrayList<>();

        for (Place place : places) {
            items.add(new PlaceItem(context, place));
        }
        super.updateDataSet(items);
        this.notifyDataSetChanged();
    }

    @Override
    public void updateItem(@NonNull PlaceItem item) {
        super.updateItem(item);
    }

}


