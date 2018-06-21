package com.example.angel.silentplaces;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.example.angel.silentplaces.utils.GeoFenceService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GeoFences {

    private static final int HOURS_AVAILABLE = 24;
    private static final int FENCE_RADIUS = 50;
    private static List<Geofence> geofences = new ArrayList<>();

    private static PendingIntent mGeofencePendingIntent = null;

    //todo completar este codigo para que llame a geofences register y unregister
    //todo hacer que sea posible escribir este gódigo de forma aislada para una sola fence cuando se añada una.
    //comp iniciar el servicio de geofence

    public static void updateGeoFences(List<Place> places) {
        for (Place place : places) {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(place.getId())
                    .setExpirationDuration(TimeUnit.HOURS.toMillis(HOURS_AVAILABLE))
                    .setCircularRegion(place.getLatLng().latitude, place.getLatLng().longitude, FENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            geofences.add(geofence);
        }

    }


    public static void geofencesRegister(Context context, GoogleApiClient googleApiClient) {

        if (geofences == null || geofences.size() < 1) return;
        if (googleApiClient == null || !googleApiClient.isConnected()) return;


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Task<Void> addGoefencestask = LocationServices.getGeofencingClient(context).addGeofences(getGeofencingRequest(geofences), getGeofencePendingIntent(context));
            addGoefencestask.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void avoid) {

                }
            });

            addGoefencestask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }


    public static void geofencesUnregister(Context context, GoogleApiClient googleApiClient) {
        if (googleApiClient == null || !googleApiClient.isConnected()) return;
        Task<Void> unregisterFencesTask = LocationServices.getGeofencingClient(context).removeGeofences(getGeofencePendingIntent(context));
        unregisterFencesTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }


    public static void geofencesUnregister(Context context, GoogleApiClient googleApiClient, List<String> idGeofences) {
        if (googleApiClient == null || !googleApiClient.isConnected()) return;
        Task<Void> unregisterFencesTask = LocationServices.getGeofencingClient(context).removeGeofences(idGeofences);
        unregisterFencesTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }

    public static PendingIntent getGeofencePendingIntent(Context context) {

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(context, GeoFenceService.class);

        mGeofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private static GeofencingRequest getGeofencingRequest(List<Geofence> geofencesList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofencesList);
        return builder.build();
    }

}
