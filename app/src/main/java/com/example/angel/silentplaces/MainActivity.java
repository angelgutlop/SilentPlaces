package com.example.angel.silentplaces;

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
import android.text.TextUtils;
import android.view.View;

import com.example.angel.silentplaces.provider.PlacesProvider;
import com.example.angel.silentplaces.recycler.PlacesAdapter;
import com.master.permissionhelper.PermissionHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private static final int PLACES_LOADER_ID = 1;
    private static final int PERSMISSIONS_LOCATION_INTERNTET_CODE = 100;
    //Todo localizar permisos

    //PermissionHelper permissionHelperLocalization = new PermissionHelper(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, PERSMISSIONS_LOCATION_INTERNTET_CODE);

    private PlacesAdapter placesAdapter;
    @BindView(R.id.placesRecyclerView)
    public RecyclerView placesRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Timber.plant();

        placesAdapter = new PlacesAdapter(this);
        placesRecyclerView.setAdapter(placesAdapter);
        placesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(PLACES_LOADER_ID, null, this);

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

    @OnClick(R.id.add_place_button)
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.add_place_button:
                //Todo verificar permisos
                // permissionHelperLocalization.request(permissionLocalizationCallback);
                return;
        }
    }

    PermissionHelper.PermissionCallback permissionLocalizationCallback = new PermissionHelper.PermissionCallback() {

        @Override
        public void onPermissionDeniedBySystem() {

        }

        @Override
        public void onPermissionDenied() {

        }

        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onIndividualPermissionGranted(String[] grantedPermission) {
            Timber.d("onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",", grantedPermission) + "]");
        }
    };
}
