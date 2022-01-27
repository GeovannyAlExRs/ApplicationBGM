package com.ec.bgm.movil.application.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class RouteProvider {

    CollectionReference collectionReference;

    public RouteProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Route");
    }

    public Task<QuerySnapshot> findAllRoute() {
        return collectionReference.whereEqualTo("rou_status", true).get();
    }

    public Task<QuerySnapshot> findRouteByID(String routeID) {
        return collectionReference.whereEqualTo("rou_id", routeID).get();
    }

    public Task<DocumentSnapshot> getRoute(String idRoute) {
        return collectionReference.document(idRoute).get();
    }

    public Task<QuerySnapshot> findRouteByName(String routeName) {
        return collectionReference.whereEqualTo("rou_name", routeName).get();
    }

    public Task<DocumentSnapshot> getRouteByID(String idRoute) {
        return collectionReference.document(idRoute).get();
    }
}
