package com.ec.bgm.movil.application.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.activities.MainActivity;
import com.ec.bgm.movil.application.providers.PermissionsRequestor;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;

public class MapsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private PermissionsRequestor permissionsRequestor;
    private MapView mapView;

    private AppCompatButton ac_btn_maps;

    SharedPreferences sharedPreferencesUser; // identificar el tipo de usuario
    private static final String TYPE_USER = "typeUser";

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        getViewId(view, savedInstanceState);

        validationTypeUser(view);

        mapView.setOnReadyListener(new MapView.OnReadyListener() {
            @Override
            public void onMapViewReady() {
                // This will be called each time after this activity is resumed.
                // It will not be called before the first map scene was loaded.
                // Any code that requires map data may not work as expected beforehand.
                Log.d(TAG, "HERE Rendering Engine attached.");
            }
        });

        handleAndroidPermissions();

        return view;
    }

    private void validationTypeUser(View view) {
        sharedPreferencesUser = MapsFragment.this.getActivity().getSharedPreferences(TYPE_USER, Context.MODE_PRIVATE);
        String selectUser = sharedPreferencesUser.getString("user", "");

        if (selectUser == "empleado") {
            Toast.makeText(MapsFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MapsFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();
            ac_btn_maps.setVisibility(view.GONE);
        }
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(MapsFragment.this.getActivity());
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                loadMapScene();
            }

            @Override
            public void permissionsDenied() {
                Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
    }

    private void loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.

        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    double distanceInMeters = 1000 * 50;
                    mapView.getCamera().lookAt(
                            new GeoCoordinates(-2.3, -80.7), distanceInMeters);
                } else {
                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        //platformPositioningProvider.stopLocating();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_maps:
                //goToView(PruebaActivity.class, false);
                Toast.makeText(MapsFragment.this.getActivity(), "ERROR LOCATION", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void goToView(Class activiyClass, boolean band) {
        Intent intent = new Intent(MapsFragment.this.getActivity(), activiyClass);

        if (band == false) {
            startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void getViewId(View v, Bundle instanceState) {
        ac_btn_maps = v.findViewById(R.id.id_btn_maps);
        ac_btn_maps.setOnClickListener(this);

        mapView = v.findViewById(R.id.id_map_view_here);
        mapView.onCreate(instanceState);
    }
}