package com.example.angel.silentplaces;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.example.angel.silentplaces.provider.PlacesContract;
import com.example.angel.silentplaces.provider.PlacesProvider;
import com.example.angel.silentplaces.recycler.PlacesAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.master.permissionhelper.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int PLACES_LOADER_ID = 001;

    private static final int PERSMISSIONS_LOCATION_CODE = 100;

    private static final int PLACE_PICKER_REQUEST = 200;

    PermissionHelper permissionHelperLocalization;

    private static GoogleApiClient googleApiClient;


    private PlacesAdapter placesAdapter;
    @BindView(R.id.placesRecyclerView)
    public RecyclerView placesRecyclerView;
    @BindView(R.id.permisos_localization_checkBox)
    public CheckBox permisosCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        Timber.d("Timber log enabled");

        placesAdapter = new PlacesAdapter(this);
        placesRecyclerView.setAdapter(placesAdapter);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(PLACES_LOADER_ID, null, this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        //Configura los permisos
        String[] permisosLocalizacion = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        permissionHelperLocalization = new PermissionHelper(this, permisosLocalizacion, PERSMISSIONS_LOCATION_CODE);
        permisosCheckBox.setChecked(permissionHelperLocalization.checkSelfPermission(permisosLocalizacion));

    }


    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case PLACES_LOADER_ID:
                return new CursorLoader(this, PlacesProvider.PlacesTable.CONTENT_URI_PLACES, null, null, null, null);
            default:
                return null;
        }


    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {

        int id = loader.getId();
        switch (id) {
            case PLACES_LOADER_ID:
                Cursor dataCursor = (Cursor) data;
                placesAdapter.updateDataSet(dataCursor);
                return;
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @OnClick({R.id.add_place_button, R.id.permisos_localization_checkBox})
    public void onClick(View v) {


        int id = v.getId();

        switch (id) {
            case R.id.add_place_button:

                permissionHelperLocalization.request(new PermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        permisosCheckBox.setChecked(true);
                        try {
                            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                            Intent intent = intentBuilder.build(MainActivity.this);
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onIndividualPermissionGranted(String[] strings) {
                    }

                    @Override
                    public void onPermissionDenied() {
                        permisosCheckBox.setChecked(false);

                    }

                    @Override
                    public void onPermissionDeniedBySystem() {
                        permisosCheckBox.setChecked(false);
                    }
                });
                return;

            case R.id.permisos_localization_checkBox:
                permissionHelperLocalization.request(new PermissionHelper.PermissionCallback() {

                    @Override
                    public void onPermissionGranted() {
                        permisosCheckBox.setChecked(true);
                    }

                    @Override
                    public void onPermissionDeniedBySystem() {
                        permisosCheckBox.setChecked(false);
                    }

                    @Override
                    public void onPermissionDenied() {
                        permisosCheckBox.setChecked(false);
                    }

                    @Override
                    public void onIndividualPermissionGranted(String[] strings) {
                    }

                });

                break;

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place != null) {
                String placename = place.getName().toString();
                String placeAddress = place.getAddress().toString();
                String placeId = place.getId();

                ContentValues contentValues = new ContentValues();
                contentValues.put(PlacesContract.PlaceId, placeId);
                ContentResolver contentResolver = this.getContentResolver();
                contentResolver.insert(PlacesProvider.PlacesTable.CONTENT_URI_PLACES, contentValues);
                Timber.d("Lugar seleccionado=%s", placename);
            }


        }
    }


    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERSMISSIONS_LOCATION_CODE:
                permissionHelperLocalization.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    //Control del estado de la api de google play
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
