package com.example.angel.silentplaces;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;

import com.example.angel.silentplaces.provider.PlacesContract;
import com.example.angel.silentplaces.provider.PlacesProvider;
import com.example.angel.silentplaces.recycler.PlaceItem;
import com.example.angel.silentplaces.recycler.PlacesAdapter;
import com.example.angel.silentplaces.utils.GeoFenceService;
import com.example.angel.silentplaces.utils.Permissions;
import com.example.angel.silentplaces.utils.PlacesUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.master.permissionhelper.PermissionHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ActionMode.Callback,
        FlexibleAdapter.OnItemClickListener, FlexibleAdapter.OnItemLongClickListener {

    private static final int PLACES_LOADER_ID = 001;
    private static final int PERSMISSIONS_LOCATION_CODE = 100;
    private static final int PLACE_PICKER_REQUEST = 200;
    private static final int SYSTEM_SILENT_MODE_REQUEST = 201;


    public static Boolean SILENT_MODE_ALLOWED = false;
    PermissionHelper permissionHelperLocalization;

    String[] permisosLocalizacion = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    private static GoogleApiClient googleApiClient;

    private PlacesAdapter placesAdapter;
    @BindView(R.id.permisos_localization_checkBox)
    public CheckBox permisosCheckBox;
    @BindView(R.id.enable_switch)
    public Switch enableGeofencesSwitch;
    @BindView(R.id.placesRecyclerView)
    public RecyclerView placesRecyclerView;
    @BindView(R.id.enable_silent_mode_switch)
    public Switch enableSilentModeSwitch;

    private ActionMode mActionMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());

        Timber.d("Timber log enabled");


        placesAdapter = new PlacesAdapter(this);
        placesAdapter.setMode(SelectableAdapter.Mode.MULTI);
        placesAdapter.addListener(this);

        placesRecyclerView.setAdapter(placesAdapter);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        getSupportLoaderManager().initLoader(PLACES_LOADER_ID, null, this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();


        startService(new Intent(this, GeoFenceService.class));

        //Configura los permisos
        permissionHelperLocalization = new PermissionHelper(this, permisosLocalizacion, PERSMISSIONS_LOCATION_CODE);
        permisosCheckBox.setChecked(permissionHelperLocalization.checkSelfPermission(permisosLocalizacion));


        //Verifica los permisos de acceso al volumen del dispositivo
        verifySilentMode(true);

    }

    private void verifySilentMode(Boolean startAct) {
        if (SILENT_MODE_ALLOWED == true) return;

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 24 && !nm.areNotificationsEnabled()) {
            if (startAct) {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivityForResult(intent, SYSTEM_SILENT_MODE_REQUEST);
            } else {
                Toast.makeText(this, "Silent mode not allowed", Toast.LENGTH_SHORT).show();
                SILENT_MODE_ALLOWED = false;
                enableSilentModeSwitch.setChecked(false);
                enableSilentModeSwitch.setEnabled(true);
            }
        } else {
            SILENT_MODE_ALLOWED = true;
            enableSilentModeSwitch.setChecked(true);
            enableSilentModeSwitch.setEnabled(false);
        }
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
                PlacesUtils.PlacesDecoce(this, dataCursor, new PlacesUtils.placesDecodeTask() {
                    @Override
                    public void onPlacesReady(List<Place> places) {
                        placesAdapter.updatePlaces(places);
                        GeoFences.updateGeoFences(places);

                        if (enableGeofencesSwitch.isChecked()) {
                            GeoFences.geofencesRegister(MainActivity.this, googleApiClient);
                        } else {
                            GeoFences.geofencesUnregister(MainActivity.this, googleApiClient);
                        }
                    }
                });


                return;
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    ///Click sobre los elementos principales de la actividad
    @OnClick({R.id.add_place_button, R.id.permisos_localization_checkBox, R.id.enable_switch, R.id.enable_silent_mode_switch})
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.add_place_button:

                Permissions.RequestPermuissions(permissionHelperLocalization, new Permissions.onRequestPermissions() {
                    @Override
                    public void permissionGranted(Boolean allGranted, String[] string) {
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
                    public void permissionDenied() {
                        permisosCheckBox.setChecked(false);
                    }
                });
                return;

            case R.id.permisos_localization_checkBox:

                Permissions.RequestPermuissions(permissionHelperLocalization, new Permissions.onRequestPermissions() {
                    @Override
                    public void permissionGranted(Boolean allGranted, String[] string) {
                        permisosCheckBox.setChecked(true);
                    }

                    @Override
                    public void permissionDenied() {
                        permisosCheckBox.setChecked(false);
                    }
                });

                return;

            case (R.id.enable_switch):

                Permissions.RequestPermuissions(permissionHelperLocalization, new Permissions.onRequestPermissions() {
                    @Override
                    public void permissionGranted(Boolean allGranted, String[] string) {
                        permisosCheckBox.setChecked(true);

                        if (enableGeofencesSwitch.isChecked()) {
                            GeoFences.geofencesRegister(MainActivity.this, googleApiClient);
                        } else {
                            GeoFences.geofencesUnregister(MainActivity.this, googleApiClient);
                        }

                    }

                    @Override
                    public void permissionDenied() {
                        permisosCheckBox.setChecked(false);
                    }
                });

                return;

            case (R.id.enable_silent_mode_switch):
                verifySilentMode(true);
                return;
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);

            if (place != null) {
                String placeId = place.getId();

                ContentValues contentValues = new ContentValues();
                contentValues.put(PlacesContract.PlaceId, placeId);
                ContentResolver contentResolver = this.getContentResolver();
                //Todo comprobar que no existe otro lugar con el mismo id
                contentResolver.insert(PlacesProvider.PlacesTable.CONTENT_URI_PLACES, contentValues);
                Timber.d("Lugar seleccionado=%s", place.getName().toString());
            }
        } else if (requestCode == SYSTEM_SILENT_MODE_REQUEST) {
            verifySilentMode(false);
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

    //Eventos asociados al reciclerview
    //------------------------------------------------


    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_menu_main_delete:
                ContentResolver contentResolver = getContentResolver();

                for (int pos : placesAdapter.getSelectedPositions()) {
                    PlaceItem placeItem = placesAdapter.getItem(pos);
                    String placeid = placeItem.getPlaceId();

                    contentResolver.delete(PlacesProvider.PlacesTable.withPlace(placeid), null, null);
                }
        }
        mActionMode.finish();

        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.action_menu_main, menu);
        placesAdapter.setMode(SelectableAdapter.Mode.MULTI);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        placesAdapter.clearSelection();
        placesAdapter.setMode(SelectableAdapter.Mode.IDLE);
        mActionMode = null;
    }

    @Override
    public void onItemLongClick(int position) {
        if (mActionMode == null) {
            mActionMode = startSupportActionMode(this);
        }
        toggleSelection(position);

    }


    @Override
    public boolean onItemClick(View view, int position) {
        if (mActionMode != null && position != RecyclerView.NO_POSITION) {
            // Mark the position selected
            toggleSelection(position);
            return true;
        } else {
            // Handle the item click listener

            // We don't need to activate anything
            return false;
        }


    }


    private void toggleSelection(int position) {
        // Mark the position selected
        placesAdapter.toggleSelection(position);
        int count = placesAdapter.getSelectedItemCount();


    }

}

