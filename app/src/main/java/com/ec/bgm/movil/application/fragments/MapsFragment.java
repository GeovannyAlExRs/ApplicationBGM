package com.ec.bgm.movil.application.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.activities.MainActivity;
import com.ec.bgm.movil.application.activities.SessionModeActivity;
import com.ec.bgm.movil.application.model.Assignes_Bus;
import com.ec.bgm.movil.application.providers.AssigneBusProvider;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.CodeQRProvider;
import com.ec.bgm.movil.application.providers.GeoFirestoreProvider;
import com.ec.bgm.movil.application.providers.PermissionsRequestor;
import com.ec.bgm.movil.application.providers.PlatformPositioningProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Location;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Waypoint;

import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MapsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private PermissionsRequestor permissionsRequestor;
    private MapView mapView;

    Toolbar toolbar;

    AuthFirebaseProvider authFirebaseProvider;
    private AppCompatButton ac_btn_maps;

    PlatformPositioningProvider platformPositioningProvider;

    SharedPreferences sharedPreferencesCode; // guardar el valor del code qr
    private static final String CODEQR = "code";

    SharedPreferences sharedPreferencesUser; // identificar el tipo de usuario
    private static final String TYPE_USER = "typeUser";

    SharedPreferences sharedPreferencesConection;
    SharedPreferences.Editor editorConection;
    private static final String BTN_CONECTION = "conection";

    MapMarker mapMarker;
    MapMarker mapMarkerUser;
    private boolean isConnect = false;
    GeoCoordinates geoCoordinatesCurrent;

    CodeQRProvider codeQRProvider;
    AssigneBusProvider assigneBusProvider;
    private String idDriverBus;
    private boolean isFirstTime = true;

    GeoFirestoreProvider geoFirestoreProvider;

    RoutingEngine routingEngine;
    List<Waypoint> waypoints = new ArrayList<>();
    List<MapMarker> mapMarkers = new ArrayList<>();
    MapPolyline mapPolyline;

    Waypoint waypointStart;
    Waypoint waypointMedium;
    Waypoint waypointDestination;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("HERE MAPS", "Iniciando  Fragment Maps");
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        showViewToolbar(view, "BusGeoMap | Mapa", false);

        getViewId(view, savedInstanceState);

        codeQRProvider = new CodeQRProvider();
        assigneBusProvider = new AssigneBusProvider();

        geoFirestoreProvider = new GeoFirestoreProvider();
        authFirebaseProvider = new AuthFirebaseProvider();

        sharedPreferencesConection = MapsFragment.this.getActivity().getSharedPreferences(BTN_CONECTION, Context.MODE_PRIVATE);
        editorConection = sharedPreferencesConection.edit();

        sharedPreferencesCode = MapsFragment.this.getActivity().getSharedPreferences(CODEQR, Context.MODE_PRIVATE);
        String idCodeQR = sharedPreferencesCode.getString("qr", "");

        //OJO VERIFICAR
        Log.d("ENTRO", "INICIO DEL MODULO - ID DEL CODE QR " + idCodeQR);
        if (!idCodeQR.isEmpty()) {
            Log.d("ENTRO", "ENTRO A LA CONDICION " + idCodeQR);
            findDataQR(idCodeQR);
        }

        //loadMapScene();

        platformPositioningProvider = new PlatformPositioningProvider(getContext());

        mapView.setOnReadyListener(new MapView.OnReadyListener() {
            @Override
            public void onMapViewReady() {
                // This will be called each time after this activity is resumed.
                // It will not be called before the first map scene was loaded.
                // Any code that requires map data may not work as expected beforehand.
                //handleAndroidPermissions();
                Log.d("HERE MAPS", "HERE Rendering Engine attached.");
            }
        });

        //addMarker();
        //startlocationMaps();

        validationTypeUser(view);
        handleAndroidPermissions();

        try {
            routingEngine = new RoutingEngine();

        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
        }

        routeHereMaps();

        return view;
    }

    private void routeHereMaps() {
        String rutaA= "RUTA - A";
        String rutaC= "RUTA - C";
        String rutaF= "RUTA - F";

        double latitudeStart = -2.2143026; //Lat Santa Elena
        double longitudeStart = -80.8670098; //Long Santa Elena

        double latitudeMedium = -2.405019; //Lat Santa Elena
        double longitudeMedium = -80.693607; //Long Santa Elena

        double latitudeDestination = -2.402871; //Lat Chanduy
        double longitudeDestination = -80.680492; // Long Chanduy


        GeoCoordinates startGeoCoordinates = new GeoCoordinates(latitudeStart,longitudeStart);
        GeoCoordinates mediumGeoCoordinates = new GeoCoordinates(latitudeMedium,longitudeMedium);
        GeoCoordinates destinationGeoCoordinates = new GeoCoordinates(latitudeDestination,longitudeDestination);

        waypointStart =  new Waypoint(startGeoCoordinates);
        waypointMedium = new Waypoint(mediumGeoCoordinates);
        waypointDestination = new Waypoint(destinationGeoCoordinates);

        waypoints = new ArrayList<>(Arrays.asList(waypointStart,waypointMedium,waypointDestination));

        routingEngine.calculateRoute(waypoints,
                new CarOptions(),
                new CalculateRouteCallback() {
                    @Override
                    public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                        if (routingError == null) {
                            Route route = routes.get(0);
                            showRouteMap(route);
                        } else {
                            Log.d("HERE MAPS", "Error while calculating a route:" + routingError.toString());
                        }
                    }
                });
    }

    private void showRouteMap(Route route) {
        GeoPolyline routeGeoPolyline;
        try {
            routeGeoPolyline = new GeoPolyline(route.getPolyline());
        } catch (InstantiationErrorException e) {
            return;
        }

        float widthInPixels = 20;
        Color color = new Color((short) 0x00, (short) 0x90, (short) 0x8A, (short) 0xA0);

        mapPolyline = new MapPolyline(routeGeoPolyline, widthInPixels, color);

        mapView.getMapScene().addMapPolyline(mapPolyline);
    }

    private void startlocationMaps() {
        Log.d("HERE MAPS", "Start Location...");
        platformPositioningProvider.startLocating(new PlatformPositioningProvider.PlatformLocationListener() {
            @Override
            public void onLocationUpdated(android.location.Location location) {
                Log.d("HERE MAPS", "Entro al metodo onLocationUpdated()");
                // ...
                //addMarker(view);
                ac_btn_maps.setText("Desconectar");
                isConnect = true;
                convertLocation(location);
            }
        });
    }

    private Location convertLocation(android.location.Location nativeLocation) {
        Log.d("HERE MAPS", "Entro al metodo convertLocation()");
        GeoCoordinates geoCoordinates = new GeoCoordinates(
                nativeLocation.getLatitude(),
                nativeLocation.getLongitude(),
                nativeLocation.getAltitude());

        Location location = new Location(geoCoordinates, new Date());

        if (nativeLocation.hasBearing()) {
            location.bearingInDegrees = (double) nativeLocation.getBearing();
        }
        if (nativeLocation.hasSpeed()) {
            location.speedInMetersPerSecond = (double) nativeLocation.getSpeed();
        }
        if (nativeLocation.hasAccuracy()) {
            location.horizontalAccuracyInMeters = (double) nativeLocation.getAccuracy();
        }
        addMarker(geoCoordinates);

        return location;
    }

    private void addMarker(GeoCoordinates geoCoordinates) {
        try {
            sharedPreferencesUser = MapsFragment.this.getActivity().getSharedPreferences(TYPE_USER, Context.MODE_PRIVATE);
            String selectUser = sharedPreferencesUser.getString("user", "");

            if (selectUser.equals("empleado")) {
                Log.d("HERE MAPS", "(User Empleado) Crea Marker en el mapa Coordenadas Long[" + geoCoordinates.latitude + "], Lat[" + geoCoordinates.longitude +"], alt[" + geoCoordinates.altitude +"]");
                if (mapMarker != null) {
                    mapView.getMapScene().removeMapMarker(mapMarker);
                }

                MapImage mapImage = MapImageFactory.fromResource(this.getResources(), R.drawable.bgm_marker);
                Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                geoCoordinatesCurrent = new GeoCoordinates(geoCoordinates.latitude, geoCoordinates.longitude);
                mapMarker = new MapMarker(geoCoordinatesCurrent, mapImage, anchor2D);
                mapView.getMapScene().addMapMarker(mapMarker);
                String idDriverUser = authFirebaseProvider.getUidFirebase();
                locationsBus(idDriverUser);
            }
            if (selectUser.equals("invitado")) {
                Log.d("HERE MAPS", "(User Invitado) Crea Marker en el mapa Coordenadas Long[" + geoCoordinates.latitude + "], Lat[" + geoCoordinates.longitude +"], alt[" + geoCoordinates.altitude +"]");

                if (mapMarkerUser != null) {
                    mapView.getMapScene().removeMapMarker(mapMarkerUser);
                }

                MapImage mapImage = MapImageFactory.fromResource(this.getResources(), R.drawable.bgm_marker_user);
                Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                GeoCoordinates geoCoordinatesUser = new GeoCoordinates(geoCoordinates.latitude, geoCoordinates.longitude);
                mapMarkerUser = new MapMarker(geoCoordinatesUser, mapImage, anchor2D);
                mapView.getMapScene().addMapMarker(mapMarkerUser);

                if (isFirstTime) {
                    Log.d("HERE MAPS", "(User Invitado) entro a la bandera");
                    isFirstTime = false;
                    getLocationsBus(geoCoordinatesUser);
                }
            }

        } catch (Exception e) {
            Log.d("ENTRO", "Error Fatal: " + e);
        }
    }

    private void locationsBus(String idDriverUser) {
        if (authFirebaseProvider.getUserSession()!=null) {
            geoFirestoreProvider.saveLocations(idDriverUser, geoCoordinatesCurrent);
        }
    }

    private void getLocationsBus(GeoCoordinates geoCoordinates) {
        geoFirestoreProvider.getLocationsBus(geoCoordinates).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                if (documentSnapshot.exists()) {
                    String idUser = documentSnapshot.getId();
                    Log.d("HERE MAPS", "(LocationsBus) ENTRO AL METODO getLocationsBus() Id Documento: "+ idUser + " ID USER SCANNER: " + idDriverBus);
                    if (idUser.equals(idDriverBus)) {
                        Log.d("HERE MAPS", "(LocationsBus) Existe el Id Documento: "+ idUser);
                        MapImage mapImage = MapImageFactory.fromResource(MapsFragment.this.getResources(), R.drawable.bgm_marker);
                        Anchor2D anchor2D = new Anchor2D(0.5F, 1);
                        mapMarker = new MapMarker(new GeoCoordinates(geoPoint.getLatitude(), geoPoint.getLongitude()), mapImage, anchor2D);
                        mapView.getMapScene().addMapMarker(mapMarker);
                    }
                }
            }

            @Override
            public void onDocumentExited(DocumentSnapshot documentSnapshot) {
                //Elimina localizacion del marcador existente
                if (documentSnapshot.exists()) {
                    String idUser = documentSnapshot.getId();
                    if (idUser.equals(idDriverBus)) {
                        mapView.getMapScene().removeMapMarker(mapMarker);
                    }
                }
            }

            @Override
            public void onDocumentMoved(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                if (documentSnapshot.exists()) {
                    String idUser = documentSnapshot.getId();
                    if (idUser.equals(idDriverBus)) {
                        mapMarker.setCoordinates(new GeoCoordinates(geoPoint.getLatitude(), geoPoint.getLongitude()));
                    }
                }
            }

            @Override
            public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(Exception e) {

            }
        });
    }

    private void loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                if (mapError == null) {
                    Log.d("HERE MAPS", "Cargando Maps...");
                    double distanceInMeters = 1000 * 65;
                    mapView.getCamera().lookAt(new GeoCoordinates(-2.3, -80.78), distanceInMeters);

                } else {
                    Log.d("HERE MAPS", "Loading map failed: mapError: " + mapError.name());
                    //Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                }
            }
        });
    }

    private void handleAndroidPermissions() {
        permissionsRequestor = new PermissionsRequestor(MapsFragment.this.getActivity());
        permissionsRequestor.request(new PermissionsRequestor.ResultListener(){

            @Override
            public void permissionsGranted() {
                Log.d("HERE MAPS", "Metodo permissionsGranted()");
                loadMapScene();
                //addMarker();
                //startlocationMaps();
            }

            @Override
            public void permissionsDenied() {
                Log.d("HERE MAPS", "Permissions denied by user.");
                //loadMapScene();
                //Log.e(TAG, "Permissions denied by user.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsRequestor.onRequestPermissionsResult(requestCode, grantResults);
        Log.d("HERE MAPS", "Metodo onRequestPermissionsResult()");
        //loadMapScene();
        //startlocationMaps();
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

    private void findDataQR(String idQR) {
        codeQRProvider.readCodeQRByID(idQR).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("gqr_asb_bus_id")) {
                        String idAssigneBus = documentSnapshot.getString("gqr_asb_bus_id");
                        findDataAssigneBus(idAssigneBus);
                    }

                }
            }
        });
    }

    // Buscar Documento ASSIGNE BUS por el ID del Documento
    private void findDataAssigneBus(String id) {
        Log.d("ENTRO", "ID ASSIGNE BUS " + id);
        assigneBusProvider.getAsigneBus(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Assignes_Bus assbus = documentSnapshot.toObject(Assignes_Bus.class);
                    idDriverBus = assbus.getAsb_accompanist_id();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_maps:
                if (isConnect) {
                    disconnect();
                } else {
                    startlocationMaps();
                }
                break;
            case R.id.id_action_logout:
                disconnect();
                authFirebaseProvider.logout();
                goToView(SessionModeActivity.class, true);
                break;
        }
    }

    private void disconnect() {
        editorConection.putString("action", "Conectar");
        editorConection.apply();
        ac_btn_maps.setText("Conectar");
        isConnect = false;
        platformPositioningProvider.stopLocating();

        if (authFirebaseProvider.getUserSession()!=null) {
            geoFirestoreProvider.removeLocations(authFirebaseProvider.getUidFirebase());
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

    private void showViewToolbar(View view, String title, boolean upbtn) {
        toolbar = view.findViewById(R.id.id_toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(upbtn);
    }

    private void validationTypeUser(View view) {
        sharedPreferencesUser = MapsFragment.this.getActivity().getSharedPreferences(TYPE_USER, Context.MODE_PRIVATE);
        String selectUser = sharedPreferencesUser.getString("user", "");

        if (selectUser.equals("empleado")) {
            Toast.makeText(MapsFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();
            ac_btn_maps.setVisibility(view.VISIBLE);
        }
        if (selectUser.equals("invitado")) {
            Toast.makeText(MapsFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();
            ac_btn_maps.setVisibility(view.GONE);
            startlocationMaps();
        }
    }


    private void getViewId(View v, Bundle instanceState) {
        ac_btn_maps = v.findViewById(R.id.id_btn_maps);
        ac_btn_maps.setOnClickListener(this);

        mapView = v.findViewById(R.id.id_map_view_here);
        mapView.onCreate(instanceState);
    }
}