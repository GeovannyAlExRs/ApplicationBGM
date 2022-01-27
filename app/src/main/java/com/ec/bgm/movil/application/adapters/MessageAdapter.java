package com.ec.bgm.movil.application.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.activities.ChatsActivity;
import com.ec.bgm.movil.application.model.Chat;
import com.ec.bgm.movil.application.model.Message;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.UserGuestProvider;
import com.ec.bgm.movil.application.providers.UsersProvider;
import com.ec.bgm.movil.application.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.ViewHolderMessage> {

    Context context;

    UsersProvider usersProvider;
    UserGuestProvider userGuestProvider;
    AuthFirebaseProvider authFirebaseProvider;

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, Context context) {
        super(options);

        this.context =context;

        usersProvider = new UsersProvider();
        userGuestProvider = new UserGuestProvider();
        authFirebaseProvider = new AuthFirebaseProvider();

        Log.d("ENTRO", "INICIO EL ADAPTERS CHAT");
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderMessage holder, int position, @NonNull Message message) {
        Log.d("ENTRO", "(ADAPTER) MESSAGE ");

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

        final String idMessage = documentSnapshot.getId();
        holder.crd_txt_message.setText(message.getMessage());

        String relativeTime = RelativeTime.getTimeAgo(message.getTimestamp(), context);
        holder.crd_txt_time.setText(relativeTime);

        if (message.getIdSender().equals(authFirebaseProvider.getUidFirebase())) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.setMargins(150, 0, 0, 0);
            holder.lineartContainerMessage.setLayoutParams(layoutParams);
            holder.lineartContainerMessage.setPadding(30,20,25,20);
            holder.lineartContainerMessage.setBackground(context.getResources().getDrawable(R.drawable.round_linear_message_sender));
            holder.crd_img_check.setVisibility(View.VISIBLE);
        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.setMargins(0, 0, 150, 0);
            holder.lineartContainerMessage.setLayoutParams(layoutParams);
            holder.lineartContainerMessage.setPadding(30,20,25,20);
            holder.lineartContainerMessage.setBackground(context.getResources().getDrawable(R.drawable.round_linear_message));
            holder.crd_img_check.setVisibility(View.INVISIBLE);
        }
    }

    @NonNull
    @Override
    public ViewHolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new ViewHolderMessage(view);
    }

    public class ViewHolderMessage extends RecyclerView.ViewHolder {
        TextView crd_txt_time, crd_txt_message;
        ImageView crd_img_check;
        LinearLayoutCompat lineartContainerMessage;
        View viewHolder;

        public ViewHolderMessage(@NonNull View v) {
            super(v);
            crd_txt_time = v.findViewById(R.id.id_crd_txt_time);
            crd_txt_message = v.findViewById(R.id.id_crd_txt_message);
            crd_img_check = v.findViewById(R.id.id_crd_img_check);
            lineartContainerMessage = v.findViewById(R.id.id_lineartContainerMessage);
            viewHolder = v;
        }
    }
}
