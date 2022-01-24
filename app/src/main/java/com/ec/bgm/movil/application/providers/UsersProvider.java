package com.ec.bgm.movil.application.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersProvider {

    CollectionReference collectionReference;

    public UsersProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Users");
    }
    public Task<DocumentSnapshot> getUserByID(String idBus) {
        return collectionReference.document(idBus).get();
    }
}
