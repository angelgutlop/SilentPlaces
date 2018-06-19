package com.example.angel.silentplaces.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.example.angel.silentplaces.provider.PlacesContract;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;

public class PlacesAdapter extends FlexibleAdapter<PlaceItem> {

    private Cursor mCursor;
    private Context context;


    public PlacesAdapter(Context context) {
        super(null);
        this.context = context;
    }


    public PlacesAdapter(Context context, @Nullable List<PlaceItem> items) {
        super(items);
        this.context = context;
    }

    public PlacesAdapter(Context context, Cursor cursor) {

        this(context, cursor2List(context, cursor));
        this.mCursor = cursor;
        this.context = context;
    }


    public void updateDataSet(Cursor cursor) {
        this.mCursor = cursor;
        List<PlaceItem> list = cursor2List(context, cursor);
        updateDataSet(list);
    }



    private static List<PlaceItem> cursor2List(Context context, Cursor cursor) {

        if (cursor == null) return null;

        List<PlaceItem> lista = new ArrayList<>();

        for (int i = 0; i < cursor.getCount(); i++) {
            PlaceItem item =cursor2PlaceItem(context, cursor, i);
            if(item!=null) lista.add(item);
        }

        return lista;
    }

    @Override
    public void updateDataSet(@Nullable List<PlaceItem> items) {
        super.updateDataSet(items);
    }

    @Override
    public void updateItem(@NonNull PlaceItem item) {
        super.updateItem(item);
    }



    private static PlaceItem cursor2PlaceItem(Context context, Cursor cursor, int i){

        if(cursor==null) return null;
        if(cursor.getCount()-1<i) return null;

        cursor.moveToPosition(i);

        int columnaName= cursor.getColumnIndex(PlacesContract.PlaceName);
        int columnaDireccion= cursor.getColumnIndex(PlacesContract.PlaceAddres);

        String name = cursor.getString(columnaName);
        String direccion = cursor.getString(columnaDireccion);

        return new PlaceItem(context,name, direccion);
    }

}
