package com.ec.bgm.movil.application.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.activities.ChatsActivity;
import com.ec.bgm.movil.application.activities.SessionModeActivity;
import com.ec.bgm.movil.application.adapters.Stops1Adapter;
import com.ec.bgm.movil.application.adapters.Stops2Adapter;
import com.ec.bgm.movil.application.model.Chat;
import com.ec.bgm.movil.application.model.Place;
import com.ec.bgm.movil.application.model.Stops;
import com.ec.bgm.movil.application.providers.AssigneBusProvider;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.BusProvider;
import com.ec.bgm.movil.application.providers.ChatProvider;
import com.ec.bgm.movil.application.providers.CodeQRProvider;
import com.ec.bgm.movil.application.providers.PlaceProvider;
import com.ec.bgm.movil.application.providers.StopsProvider;
import com.ec.bgm.movil.application.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class StopsFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerview_stop1, recyclerview_stop2;

    LinearLayoutManager linearLayoutManager;

    View view;
    Toolbar toolbar;

    Stops1Adapter stops1Adapter;
    Stops2Adapter stops2Adapter;

    ChatProvider chatProvider;
    AuthFirebaseProvider authFirebaseProvider;
    StopsProvider stopsProvider;

    SharedPreferences sharedPreferencesUser; // identificar el tipo de usuario
    private static final String TYPE_USER = "typeUser";

    SharedPreferences sharedPreferencesUserAccompanist; // identificar el tipo de usuario
    private static final String USER_ACCOMPANIST = "userAccompanist";

    String idSession;
    String idChat;

    boolean band;

    public StopsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_stops, container, false);
        showViewToolbar(view, "BusGeoMap | Paradas", false);
        getViewId();

        authFirebaseProvider = new AuthFirebaseProvider();
        chatProvider = new ChatProvider();
        stopsProvider = new StopsProvider();

        validationTypeUser(view);
        return view;
    }

    private void validationTypeUser(View view) {
        sharedPreferencesUser = StopsFragment.this.getActivity().getSharedPreferences(TYPE_USER, Context.MODE_PRIVATE);
        String selectUser = sharedPreferencesUser.getString("user", "");

        if (selectUser.equals("empleado")) {
            Toast.makeText(StopsFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();
            band = true;
        }if (selectUser.equals("invitado")) {
            band = false;
            Toast.makeText(StopsFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();

            sharedPreferencesUserAccompanist = StopsFragment.this.getActivity().getSharedPreferences(USER_ACCOMPANIST, Context.MODE_PRIVATE);
            String userAccompanistID = sharedPreferencesUserAccompanist.getString("ID_ACCOMPANIST", "");

            idSession = authFirebaseProvider.getUidFirebase();
            idChat = idSession + userAccompanistID;

            Log.d("ENTRO", "ID DEL OFICIAL " + userAccompanistID + " ID SESSION " + idSession + " CONCATENADO ID_CHAT " + idChat);
        }
    }

    private void getChat() {
        chatProvider.getAllChat(idSession).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Chat chat = document.toObject(Chat.class);
                            idChat = chat.getIdChat();
                        }
                    }
                }
            }
        });
    }

    private void readChatID() {
        Log.d("ENTRO", "METODO PARA RECUPERAR el IDCHAT readChatID()");
        chatProvider.getAllChat(idSession).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Chat chat = document.toObject(Chat.class);
                            Log.d("ENTRO", "(CHAT) Id del CHAT RECUPERADO " + chat.getIdChat());
                            idChat = chat.getIdChat();
                        }
                    }
                }
            }
        });

    }

    private void getAllChat() {
        String idSessionAccompanist = authFirebaseProvider.getUidFirebase();
        Query query = stopsProvider.findStopsByReceiver(idSessionAccompanist);

        FirestoreRecyclerOptions<Stops> options = new  FirestoreRecyclerOptions.Builder<Stops>()
                .setQuery(query, Stops.class)
                .build();

        stops2Adapter = new Stops2Adapter(options, getContext());
        stops2Adapter.notifyDataSetChanged();
        recyclerview_stop1.setAdapter(stops2Adapter);
        stops2Adapter.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (band == true) {
            getAllChat();
        } else {
            getAllStops1RecyclerView();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (band == true) {
            stops2Adapter.stopListening(); // ADAPTER ACCOMPANIST
        } else {
            stops1Adapter.stopListening(); // ADAPTER USER GUEST
        }
    }

    private void getAllStops1RecyclerView() {
        Log.d("ENTRO", "Entro al metododel ONStart getAllStops1RecyclerView() ID CHAT " + idChat);
        Query query = stopsProvider.getStopsByChats(idChat);

        FirestoreRecyclerOptions<Stops> options = new  FirestoreRecyclerOptions.Builder<Stops>()
                .setQuery(query, Stops.class)
                .build();

        stops1Adapter = new Stops1Adapter(options, StopsFragment.this.getActivity());
        //stops1Adapter.notifyDataSetChanged();
        recyclerview_stop1.setAdapter(stops1Adapter);

        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH");
        Date dateCurrent = new Date(new Date().getTime());
        final String fechaActual = dateformat.format(dateCurrent);

        deleteStops(fechaActual).attachToRecyclerView(recyclerview_stop1);

        stops1Adapter.startListening();
    }

    private ItemTouchHelper deleteStops(final String dateCurrent) {
        ItemTouchHelper itemTouch = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                stops1Adapter.deleteItem(viewHolder.getAdapterPosition(), dateCurrent);
                stops1Adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(StopsFragment.this.getActivity(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(StopsFragment.this.getActivity(), R.color.colorDelete))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(StopsFragment.this.getActivity(), R.color.colorDelete))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep)
                        .addSwipeRightActionIcon(R.drawable.ic_delete_sweep)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        return itemTouch;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_action_logout:
                authFirebaseProvider.logout();
                goToView(SessionModeActivity.class, true);
                clearViews();
                break;
        }
    }

    private void clearViews() {
        idChat = "";
        idSession = "";
    }

    private void goToView(Class activiyClass, boolean band) {
        Intent intent = new Intent(StopsFragment.this.getActivity(), activiyClass);

        if (band == false) {
            startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void getViewId() {

        recyclerview_stop1 = view.findViewById(R.id.id_recyclerview_stop1);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerview_stop1.setLayoutManager(linearLayoutManager);

        recyclerview_stop2 = view.findViewById(R.id.id_recyclerview_stop2);
    }

    private void showViewToolbar(View view, String title, boolean upbtn) {
        toolbar = view.findViewById(R.id.id_toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(upbtn);
    }
}