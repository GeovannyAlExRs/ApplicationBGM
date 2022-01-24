package com.ec.bgm.movil.application.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.model.Schedule;
import com.ec.bgm.movil.application.providers.AssigneBusProvider;
import com.ec.bgm.movil.application.providers.BusProvider;
import com.ec.bgm.movil.application.providers.PlaceProvider;
import com.ec.bgm.movil.application.providers.RouteProvider;
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

public class ScheduleAdapter extends FirestoreRecyclerAdapter<Schedule, ScheduleAdapter.ViewHolderSchedule> {

    Context context;

    AssigneBusProvider assigneBusProvider;
    BusProvider busProvider;

    RouteProvider routeProvider;
    PlaceProvider placeProvider;

    public ScheduleAdapter(@NonNull FirestoreRecyclerOptions<Schedule> options, Context context) {
        super(options);

        this.context =context;

        assigneBusProvider = new AssigneBusProvider();
        busProvider = new BusProvider();

        routeProvider = new RouteProvider();
        placeProvider = new PlaceProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderSchedule holder, int position, @NonNull Schedule schedule) {
        Log.d("ENTRO", "(ADAPTER) HORARIOS " + schedule.getSch_id() + " " + schedule.getSch_state());

        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
        Date time = new Date(schedule.getSch_departure_time());
        String hora = dateformat.format(time);

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

        final String idAssigneBus = documentSnapshot.getString("sch_asb_id_number_disc");
        final String idRoute = documentSnapshot.getString("sch_rou_id_name");

        findDiscAssigneBus(idAssigneBus, holder);
        findRoutePlace(idRoute, holder);

        holder.txt_sch_state.setText(schedule.getSch_state()); // Cambiar estado
        holder.txt_sch_departure_time.setText(hora);
    }

    @NonNull
    @Override
    public ViewHolderSchedule onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_schedule, parent, false);
        return new ViewHolderSchedule(view);
    }

    private void findDiscAssigneBus(String idAssigneBus, ViewHolderSchedule holder) {
        assigneBusProvider.getAsigneBus(idAssigneBus).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String idBus = documentSnapshot.getString("asb_bus_id");
                    findDataBus(idBus, holder);
                }
            }
        });
    }

    private void findDataBus(String idBus, ViewHolderSchedule holder) {
        busProvider.getBusByID(idBus).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long busDisc = documentSnapshot.getLong("bus_number_disc");
                    holder.txt_sch_number_disc_bus.setText(String.valueOf(busDisc));
                }
            }
        });
    }

    private void findRoutePlace(String idRoute, ViewHolderSchedule holder) {
        routeProvider.findRouteByID(idRoute).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idPlaceStarting = document.getString("rou_place_starting");
                        findDataPlace(idPlaceStarting, holder, 1);

                        String idPlaceDestination = document.getString("rou_place_destination");
                        findDataPlace(idPlaceDestination, holder, 2);
                    }
                }
            }
        });
    }

    private void findDataPlace(String idPlace, ViewHolderSchedule holder, int option) {
        placeProvider.findSchedule(idPlace).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String placeName = documentSnapshot.getString("pla_name");
                    if (option == 1) {
                        holder.txt_sch_place_starting.setText(placeName);
                    } else {
                        holder.txt_sch_place_destination.setText(placeName);
                    }
                }
            }
        });
    }

    public class ViewHolderSchedule extends RecyclerView.ViewHolder {
        TextView txt_sch_number_disc_bus, txt_sch_place_starting, txt_sch_place_destination, txt_sch_departure_time, txt_sch_state;
        ImageView img_ico_state_bus;

        public ViewHolderSchedule(@NonNull View v) {
            super(v);
            img_ico_state_bus = v.findViewById(R.id.id_img_ico_state_bus);
            txt_sch_number_disc_bus = v.findViewById(R.id.id_txt_sch_number_disc_bus);
            txt_sch_place_starting = v.findViewById(R.id.id_txt_sch_place_starting);
            txt_sch_place_destination = v.findViewById(R.id.id_txt_sch_place_destination);;
            txt_sch_departure_time = v.findViewById(R.id.id_txt_sch_departure_time);
            txt_sch_state = v.findViewById(R.id.id_txt_sch_status);
        }
    }
}
