package com.ec.bgm.movil.application.providers;

import com.ec.bgm.movil.application.model.UserGuest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserGuestProvider {

    CollectionReference collectionReference;

    public UserGuestProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("User_Guest");
    }

    public Task<Void> createUserGuest(UserGuest userGuest) {
        return collectionReference.document(userGuest.getId_userGuest()).set(userGuest);
    }

    public Task<DocumentSnapshot> getUsersByID(String idDoc) {
        return collectionReference.document(idDoc).get();
    }


}
