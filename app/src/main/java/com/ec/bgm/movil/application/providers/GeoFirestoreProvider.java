package com.ec.bgm.movil.application.providers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.here.sdk.core.GeoCoordinates;

import org.imperiumlabs.geofirestore.GeoFirestore;

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
}
