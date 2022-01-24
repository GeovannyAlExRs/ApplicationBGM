package com.ec.bgm.movil.application.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CodeQRProvider {

    CollectionReference collectionReference;

    public CodeQRProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("CodeQR");
    }

    public Query getCodeQRByID(String idCode) {
        return collectionReference.whereEqualTo("gqr_code" , idCode);
    }

    public Task<DocumentSnapshot> readCodeQRByID(String idCode) {
        return collectionReference.document(idCode).get();
    }

}
