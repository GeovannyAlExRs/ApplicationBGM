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
import com.ec.bgm.movil.application.model.Stops;
import com.ec.bgm.movil.application.providers.PlaceProvider;
import com.ec.bgm.movil.application.providers.StopsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Stops2Adapter extends FirestoreRecyclerAdapter<Stops, Stops2Adapter.ViewHolderStops> {

    Context context;

    StopsProvider stopsProvider;
    PlaceProvider placeProvider;

    public Stops2Adapter(@NonNull FirestoreRecyclerOptions<Stops> options, Context context) {
        super(options);

        this.context = context;

        placeProvider = new PlaceProvider();
        stopsProvider = new StopsProvider();
        Log.d("ENTRO", "INICIO EL ADAPTERS STOP2");
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderStops holder, int position, @NonNull Stops stops) {
        String objetoStops = stops.getIdStops() + " ID USER " + stops.getIdReceiver() + " ID GUEST " + stops.getIdSender() + " ID PLACE " + stops.getIdPlace();
        Log.d("ENTRO", "(ADAPTER) STOP2 " + objetoStops);

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

        final String idPlace = documentSnapshot.getString("idPlace");
        findPlaceInfo2(idPlace, holder);
        cantPlace(idPlace, holder);
    }

    private void cantPlace(String idPlace, ViewHolderStops holder) {
        stopsProvider.getStopsByPlace(idPlace).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final ArrayList<String> numStopsPlace = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.contains("idPlace")) {
                            String place = document.getString("idPlace");
                            holder.txt_place_stops.setText(place);
                            numStopsPlace.add(place);
                            //getNamePlace(place, holder);
                        }
                    }
                    int cant = numStopsPlace.size();
                    holder.txt_cant_user.setText(String.valueOf(cant));
                }
            }
        });
    }

    private void findPlaceInfo2(String idPlace, ViewHolderStops holder) {
        placeProvider.getPlaceByIDPlace(idPlace).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final ArrayList<String> stopsPlaceName = new ArrayList<>();
                    String placeName = "";
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id_place = document.getString("pla_id");
                        placeName = document.getString("pla_name");
                        //getNamePlace(placeName, holder);
                        holder.txt_place_stops.setText(placeName);
                        //stopsPlaceName.add(placeName);
                        //numUserByStop(id_place, placeName, holder);
                    }
                    /*int num = stopsPlaceName.size();
                    holder.txt_cant_user.setText(String.valueOf(num));
                    holder.txt_place_stops.setText(placeName);*/
                }
            }
        });
    }

        /*private void findUserAccompanist(String idUserAccompanist, ViewHolderStops holder) {
        stopsProvider.findStopsByUserAccompanist(idUserAccompanist).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //final ArrayList<String> stopsIdPlace = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idReceiver = document.getString("idReceiver");
                        String idSender = document.getString("idSender");
                        String idPlace = document.getString("idPlace");
                        String texto = idPlace + " " + idReceiver + " " + idSender;
                        holder.txt_place_stops.setText(texto);
                    }
                }
            }
        });
    }*/


    @NonNull
    @Override
    public ViewHolderStops onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_stop2, parent, false);
        return new ViewHolderStops(view);
    }

    private void findPlaceInfo(String idPlace, ViewHolderStops holder) {
        placeProvider.getPlaceByID(idPlace).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("pla_id")) {
                        String namePlace = documentSnapshot.getString("pla_name");
                        holder.txt_place_stops.setText(namePlace);
                    }
                }
            }
        });
    }

    public class ViewHolderStops extends RecyclerView.ViewHolder {
        TextView txt_place_stops, txt_cant_user;
        View viewHolder;

        public ViewHolderStops(@NonNull View v) {
            super(v);
            txt_place_stops = v.findViewById(R.id.id_txt_place_stops2);
            txt_cant_user = v.findViewById(R.id.id_txt_cant_user);
            viewHolder = v;
        }
    }
}
