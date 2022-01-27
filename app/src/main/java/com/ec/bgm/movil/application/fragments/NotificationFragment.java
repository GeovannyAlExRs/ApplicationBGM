package com.ec.bgm.movil.application.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.adapters.ChatAdapter;
import com.ec.bgm.movil.application.model.Chat;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.ChatProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class NotificationFragment extends Fragment {

    ChatAdapter chatAdapter;

    ChatProvider chatProvider;
    AuthFirebaseProvider authFirebaseProvider;

    View view;
    private RecyclerView recyclerview_notification;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notification, container, false);

        getIdView();

        chatProvider = new ChatProvider();
        authFirebaseProvider = new AuthFirebaseProvider();

        return view;
    }


    private void getAllChatRecyclerView() {
        Query query = chatProvider.getAllChat(authFirebaseProvider.getUidFirebase());
        Log.d("ENTRO", "Entro al onStart QUERY CHAT" + query.get());

        FirestoreRecyclerOptions<Chat> options = new  FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();

        Log.d("ENTRO", "FirestoreRecyclerview OPTIONS CHAT" +options.toString());

        chatAdapter = new ChatAdapter(options, getContext());
        chatAdapter.notifyDataSetChanged();
        recyclerview_notification.setAdapter(chatAdapter);
        chatAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllChatRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatAdapter.stopListening();
    }

    private void getIdView() {
        recyclerview_notification = view.findViewById(R.id.id_recyclerview_notification);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerview_notification.setLayoutManager(linearLayoutManager);

    }
}