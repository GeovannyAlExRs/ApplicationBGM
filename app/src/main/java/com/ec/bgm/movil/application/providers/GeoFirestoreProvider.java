package com.ec.bgm.movil.application.providers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.here.sdk.core.GeoCoordinates;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.EventListenerBridge;
import org.imperiumlabs.geofirestore.listeners.GeoQueryDataEventListener;

public class GeoFirestoreProvider {

    CollectionReference collectionReference;
    GeoFirestore geoFirestore;


    public GeoFirestoreProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("LocationsBus");
        geoFirestore = new GeoFirestore(collectionReference);
    }

    public void saveLocations(String idDriver, GeoCoordinates geoCoordinates) {
        geoFirestore.setLocation(idDriver, new GeoPoint(geoCoordinates.latitude, geoCoordinates.longitude));
    }

    public void removeLocations(String idDriver) {
        geoFirestore.removeLocation(idDriver);
    }

    public GeoQuery getLocationsBus(GeoCoordinates geoCoordinates) {
        GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(geoCoordinates.latitude, geoCoordinates.longitude), 75);
        geoQuery.removeAllListeners();
        return geoQuery;
    }
}
