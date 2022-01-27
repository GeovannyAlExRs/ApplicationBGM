package com.ec.bgm.movil.application.providers;

import android.util.Log;
import android.widget.Toast;

import com.ec.bgm.movil.application.fragments.ColectiveFragment;
import com.ec.bgm.movil.application.model.Chat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatProvider {

    CollectionReference collectionReference;

    public ChatProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat) {
        //collectionReference.document(chat.getIdUser()).collection("Users").document(chat.getIdUserGuest()).set(chat);
        collectionReference.document(chat.getIdUserGuest()+chat.getIdUser()).set(chat);
    }

    public Query getAllChat(String idUser) {
        return collectionReference.whereArrayContains("ids", idUser);
    }

    public Query findAllChat(String idUser) {
        return collectionReference.document(idUser).collection("Users"); //.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query checkUsers(String idUser, String idUserGuest) {
        ArrayList<String> ids = new ArrayList<>();

        ids.add(idUser + idUserGuest);
        ids.add( idUserGuest+ idUser);

        Log.d("ENTRO", "Verificar ID CHATS - ID USER " + ids.toString());

        return collectionReference.whereIn("idChat", ids);
    }
}
