package com.ec.bgm.movil.application.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.adapters.MessageAdapter;
import com.ec.bgm.movil.application.fragments.ColectiveFragment;
import com.ec.bgm.movil.application.model.Chat;
import com.ec.bgm.movil.application.model.Message;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.ChatProvider;
import com.ec.bgm.movil.application.providers.MessageProvider;
import com.ec.bgm.movil.application.providers.UserGuestProvider;
import com.ec.bgm.movil.application.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText text_send;
    private ImageView img_btn_send;
    private RecyclerView recyclerview_chats;

    private TextView txt_user, txt_time;
    private CircleImageView btn_circleBack;

    LinearLayoutManager linearLayoutManager;

    String idUserExtra;
    String idUserGuest;
    String idChatExtra;

    View view;

    AuthFirebaseProvider authFirebaseProvider;
    ChatProvider chatProvider;
    MessageProvider messageProvider;

    UsersProvider usersProvider;
    UserGuestProvider userGuestProvider;

    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        chatProvider = new ChatProvider();
        authFirebaseProvider = new AuthFirebaseProvider();
        messageProvider = new MessageProvider();

        usersProvider = new UsersProvider();
        userGuestProvider = new UserGuestProvider();

        getIdView();

        idUserExtra = getIntent().getStringExtra("idUser1");
        idUserGuest = getIntent().getStringExtra("idUser2");
        idChatExtra = getIntent().getStringExtra("idChats");

        showUserToolbar(R.layout.users_chat_toolbar);

        checkChatExits();
    }

    private void checkChatExits() {
        chatProvider.checkUsers(idUserGuest, idUserExtra).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    Toast.makeText(ChatsActivity.this, "NO EXISTE EL CHAT", Toast.LENGTH_SHORT).show();
                    createChat();
                } else {
                    //Toast.makeText(ChatsActivity.this, "EXISTE EL CHAT", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createChat() {
        Chat chat = new Chat();

        String id = idUserGuest+idUserExtra;
        chat.setIdChat(id);

        chat.setIdUser(idUserExtra); // ID del usuario receptor
        chat.setIdUserGuest(idUserGuest); //ID Usuario emisor
        chat.setIdPlace("");
        chat.setWritting(false);
        chat.setTimestamp(new Date().getTime());

        ArrayList<String> listIDs = new ArrayList<>();
        listIDs.add(idUserExtra);
        listIDs.add(idUserGuest);
        chat.setIds(listIDs);

        chatProvider.create(chat);

        //messageDetail(id, idUserGuest);
    }

    private void sendMessage() {
        String messageText = text_send.getText().toString();
        if (!messageText.isEmpty()) {
            Message message = new Message();

            message.setIdChat(idChatExtra);
            message.setTimestamp(new  Date().getTime());
            message.setMessage(messageText);
            message.setViewed(false);

            if (authFirebaseProvider.getUidFirebase().equals(idUserExtra)) {
                message.setIdSender(idUserExtra);
                message.setIdReceiver(idUserGuest);
            } else {
                message.setIdSender(idUserGuest);
                message.setIdReceiver(idUserExtra);
            }

            messageProvider.createMessage(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        text_send.setText("");
                        messageAdapter.notifyDataSetChanged();
                        //Toast.makeText(ChatsActivity.this, "El mensaje se envio correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatsActivity.this, "El mensaje NO se envio correctamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_Img_btn_send:
                //Toast.makeText(ChatsActivity.this, "Mensaje enviado xD...", Toast.LENGTH_SHORT).show();
                sendMessage();
                break;
            case R.id.id_circleBack:
                finish();
                break;
        }
    }

    private void getIdView() {
        text_send = findViewById(R.id.id_text_send);
        img_btn_send = findViewById(R.id.id_Img_btn_send);

        img_btn_send.setOnClickListener(this);

        recyclerview_chats = findViewById(R.id.id_recyclerview_chats);
        linearLayoutManager = new LinearLayoutManager(ChatsActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerview_chats.setLayoutManager(linearLayoutManager);
    }

    private void getAllMessageRecyclerView() {
        Query query = messageProvider.getMessageByChats(idChatExtra);
        FirestoreRecyclerOptions<Message> options = new  FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        messageAdapter = new MessageAdapter(options, ChatsActivity.this);
        recyclerview_chats.setAdapter(messageAdapter);
        messageAdapter.startListening();
        // para posicionar en el ultimo mensaje
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int numberMessage = messageAdapter.getItemCount();
                int lastMesage = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastMesage == -1 || (positionStart >= (numberMessage -1) && lastMesage == (positionStart - 1))) {
                    recyclerview_chats.scrollToPosition(positionStart);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllMessageRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
        messageAdapter.stopListening();
    }

    private void showUserToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(resource, null);

        actionBar.setCustomView(view);

        txt_user = view.findViewById(R.id.id_txt_user);
        txt_time = view.findViewById(R.id.id_txt_time);
        btn_circleBack = view.findViewById(R.id.id_circleBack);

        btn_circleBack.setOnClickListener(this);

        getUserInfo();
    }

    private void getUserInfo() {
        if (authFirebaseProvider.getUidFirebase().equals(idUserExtra)) {
            getUserGuest(idUserGuest);
        } else {
            getUser(idUserExtra);
        }
    }

    private void getUser(String id) {
        usersProvider.getUserByID(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("use_name")) {
                        String nameUser = documentSnapshot.getString("use_name");
                        txt_user.setText(nameUser);
                    }
                }
            }
        });
    }

    private void getUserGuest(String id) {
        userGuestProvider.getUsersByID(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String nameUser = documentSnapshot.getString("username");
                        txt_user.setText(nameUser);
                    }
                }
            }
        });
    }
}