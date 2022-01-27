package com.ec.bgm.movil.application.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.model.Chat;
import com.ec.bgm.movil.application.providers.UserGuestProvider;
import com.ec.bgm.movil.application.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolderChat> {

    Context context;

    UsersProvider usersProvider;
    UserGuestProvider userGuestProvider;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);

        this.context =context;

        usersProvider = new UsersProvider();
        userGuestProvider = new UserGuestProvider();

        Log.d("ENTRO", "INICIO EL ADAPTERS CHAT");
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderChat holder, int position, @NonNull Chat chat) {
        Log.d("ENTRO", "(ADAPTER) HORARIOS " + chat.getTimestamp());

        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date time = new Date(chat.getTimestamp());
        String hora = dateformat.format(time);

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

        final String idChat = documentSnapshot.getId();

        getUserInfo(idChat, holder);
        getUserGuestInfo(idChat, holder);

        holder.txt_tiempo.setText(hora);

    }

    private void getUserGuestInfo(String idChat, ViewHolderChat holder) {
        userGuestProvider.getUsersByID(idChat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String nameUser = documentSnapshot.getString("username");
                        holder.txt_name_user.setText(nameUser);
                    }
                }
            }
        });
    }

    private void getUserInfo(String idChat, final ViewHolderChat holder) {
        usersProvider.getUserByID(idChat).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String lastName = null;
                    String firstName = null;
                    if (documentSnapshot.contains("use_last_name")) {
                        lastName = documentSnapshot.getString("use_last_name");
                    }
                    if (documentSnapshot.contains("use_first_name")) {
                        firstName = documentSnapshot.getString("use_first_name");
                    }
                    holder.txt_name_user.setText(firstName + " " + lastName);
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_notification, parent, false);
        return new ViewHolderChat(view);
    }

    public class ViewHolderChat extends RecyclerView.ViewHolder {
        TextView txt_name_user, txt_message, txt_tiempo;
        View viewHolder;
        public ViewHolderChat(@NonNull View v) {
            super(v);
            txt_name_user = v.findViewById(R.id.id_txt_name_user);
            txt_message = v.findViewById(R.id.id_txt_message);
            txt_tiempo = v.findViewById(R.id.id_txt_tiempo);
            viewHolder = v;
        }
    }
}
