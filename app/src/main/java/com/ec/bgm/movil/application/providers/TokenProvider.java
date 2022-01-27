package com.ec.bgm.movil.application.providers;

import com.ec.bgm.movil.application.model.Token;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class TokenProvider {

    CollectionReference collectionReference;

    public TokenProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Tokens");
    }

    public void create(final String idUSer) {
        if (idUSer == null) {
            return;
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Token token = new Token(instanceIdResult.getToken());
                collectionReference.document(idUSer).set(token);
            }
        });
    }

    public Task<DocumentSnapshot> getToken(String idUSer) {
        return collectionReference.document(idUSer).get();
    }
}
