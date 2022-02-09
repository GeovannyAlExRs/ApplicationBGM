package com.ec.bgm.movil.application.adapters;

import android.content.Context;
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
import com.ec.bgm.movil.application.model.Message;
import com.ec.bgm.movil.application.model.Stops;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.PlaceProvider;
import com.ec.bgm.movil.application.providers.StopsProvider;
import com.ec.bgm.movil.application.providers.UserGuestProvider;
import com.ec.bgm.movil.application.providers.UsersProvider;
import com.ec.bgm.movil.application.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Stops1Adapter extends FirestoreRecyclerAdapter<Stops, Stops1Adapter.ViewHolderStops> {

    Context context;

    PlaceProvider placeProvider;
    StopsProvider stopsProvider;

    public Stops1Adapter(@NonNull FirestoreRecyclerOptions<Stops> options, Context context) {
        super(options);

        this.context = context;

        placeProvider = new PlaceProvider();
        stopsProvider = new StopsProvider();

        Log.d("ENTRO", "INICIO EL ADAPTERS STOP1");
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderStops holder, int position, @NonNull Stops stops) {
        Log.d("ENTRO", "(ADAPTER) STOPS1 - " + stops.getIdStops() + " " + stops.getMessage() );

        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date time = new Date(stops.getTimestamp());
        String hora = dateformat.format(time);

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

        holder.txt_tiempo_stops1.setText(hora);

        final String idPlace = documentSnapshot.getString("idPlace");
        findPlaceInfo(idPlace, holder);
    }

    @NonNull
    @Override
    public ViewHolderStops onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_stop1, parent, false);
        return new ViewHolderStops(view);
    }

    public void deleteItem(int position, String dateCurrent) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        long dateStop = documentSnapshot.getLong("timestamp");

        SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH");

        Date time = new Date(dateStop);
        String dateStops = dateformat.format(time);

        if (dateCurrent.equals(dateStops)) {
            Log.d("ENTRO", "Las Fechas son iguales " + dateStops + " FECHA ACTUAL " + dateCurrent);
            getSnapshots().getSnapshot(position).getReference().delete();
        } else {
            Log.d("ENTRO", "Las Fechas NO coinciden " + dateStops + " FECHA ACTUAL " + dateCurrent);
        }


        /*stopsProvider.findStopsBySender(id).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Stops stop = document.toObject(Stops.class);

                            SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH");
                            String dateStops = dateformat.format(stop.getTimestamp());

                            if (dateCurrent.equals(dateStops)) {
                                getSnapshots().getSnapshot(position).getReference().delete();
                                Log.d("ENTRO", "Las Fechas son iguales " + dateStops + " FECHA ACTUAL " + dateCurrent);
                            }else {
                                Log.d("ENTRO", "Las Fechas no coinciden " + dateStops + " FECHA ACTUAL " + dateCurrent);
                            }
                        }
                    }
                }
            }
        });*/
    }

    private void findPlaceInfo(String idPlace, ViewHolderStops holder) {
        placeProvider.getPlaceByID(idPlace).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("pla_name")) {
                        String namePlace = documentSnapshot.getString("pla_name");
                        holder.txt_place_stops1.setText(namePlace.toUpperCase());
                    }
                }
            }
        });
    }


    public class ViewHolderStops extends RecyclerView.ViewHolder {
        TextView txt_place_stops1, txt_tiempo_stops1;
        View viewHolder;

        public ViewHolderStops(@NonNull View v) {
            super(v);
            txt_place_stops1 = v.findViewById(R.id.id_txt_place_stops1);
            txt_tiempo_stops1 = v.findViewById(R.id.id_txt_tiempo_stops1);
            viewHolder = v;
        }
    }
}
