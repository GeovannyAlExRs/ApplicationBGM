package com.ec.bgm.movil.application.providers;

import com.ec.bgm.movil.application.model.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MessageProvider {

    CollectionReference collectionReference;

    public MessageProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Messages");
    }

    public Task<Void> createMessage(Message msg) {
        DocumentReference docReference = collectionReference.document();
        msg.setIdMessage(docReference.getId());

        return docReference.set(msg);
    }

    public Query getMessageByChats(String id) {
        return collectionReference.whereEqualTo("idChat", id).orderBy("timestamp", Query.Direction.ASCENDING);
    }
}
