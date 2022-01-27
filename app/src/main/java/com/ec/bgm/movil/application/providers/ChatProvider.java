package com.ec.bgm.movil.application.providers;

import com.ec.bgm.movil.application.model.Chat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatProvider {

    CollectionReference collectionReference;

    public ChatProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat) {
        collectionReference.document(chat.getIdUser()).collection("Users").document(chat.getIdUserGuest()).set(chat);
        collectionReference.document(chat.getIdUserGuest()).collection("Users").document(chat.getIdUser()).set(chat);
    }

    public Query findAllChat(String idUser) {
        return collectionReference.document(idUser).collection("Users"); //.orderBy("timestamp", Query.Direction.DESCENDING);
    }
}
