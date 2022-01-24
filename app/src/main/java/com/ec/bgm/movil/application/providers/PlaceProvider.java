package com.ec.bgm.movil.application.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlaceProvider {

    CollectionReference collectionReference;

    public PlaceProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Place");
    }

    public Task<DocumentSnapshot> findSchedule(String idSchedule) {
        return collectionReference.document(idSchedule).get();
    }
}
