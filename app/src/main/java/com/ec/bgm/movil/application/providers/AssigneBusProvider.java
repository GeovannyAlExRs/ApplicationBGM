package com.ec.bgm.movil.application.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AssigneBusProvider {

    CollectionReference collectionReference;

    public AssigneBusProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Assignes_Bus");
    }

    public Task<DocumentSnapshot> getAsigneBus(String idAssigneBu) {
        return collectionReference.document(idAssigneBu).get();
    }

    public Task<QuerySnapshot> getAsigneBusByUser(String id) {
        return collectionReference.whereEqualTo("asb_accompanist_id", id).get();
    }
}
