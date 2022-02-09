package com.ec.bgm.movil.application.providers;

import com.ec.bgm.movil.application.model.Stops;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class StopsProvider {

    CollectionReference collectionReference;

    public StopsProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Stops");
    }

    public Task<Void> createStops(Stops stops) {
        DocumentReference docReference = collectionReference.document();
        stops.setIdStops(docReference.getId());

        return docReference.set(stops);
    }

    public Query getStopsByChats(String id) {
        return collectionReference.whereEqualTo("idChat", id).orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Task<QuerySnapshot> findStopsBySender(String idSession, String isAccompanist) {
        return collectionReference.whereEqualTo("idSender", idSession).whereEqualTo("idReceiver", isAccompanist).get();
    }

    public Query findStopsByReceiver(String isAccompanist) {
        return collectionReference.whereEqualTo("idReceiver", isAccompanist).whereEqualTo("status", true);
    }

    public Task<QuerySnapshot> findStopsByUserAccompanist(String isAccompanist) {
        return collectionReference.whereEqualTo("idReceiver", isAccompanist).get();
    }

    public Task<QuerySnapshot> getStopsByPlace(String idPlace) {
        return collectionReference.whereEqualTo("idPlace", idPlace).get();
    }
}
