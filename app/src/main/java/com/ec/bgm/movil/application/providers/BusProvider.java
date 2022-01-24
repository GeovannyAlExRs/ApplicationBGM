package com.ec.bgm.movil.application.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BusProvider {

    CollectionReference collectionReference;

    public BusProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Bus");
    }
    public Task<DocumentSnapshot> getBusByID(String idBus) {
        return collectionReference.document(idBus).get();
    }

}
