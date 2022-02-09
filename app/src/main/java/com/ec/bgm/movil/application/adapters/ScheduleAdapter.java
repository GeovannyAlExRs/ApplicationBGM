package com.ec.bgm.movil.application.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.activities.ScannQrActivity;
import com.ec.bgm.movil.application.model.Schedule;
import com.ec.bgm.movil.application.providers.AssigneBusProvider;
import com.ec.bgm.movil.application.providers.BusProvider;
import com.ec.bgm.movil.application.providers.PlaceProvider;
import com.ec.bgm.movil.application.providers.RouteProvider;
import com.ec.bgm.movil.application.providers.ScheduleProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleAdapter extends FirestoreRecyclerAdapter<Schedule, ScheduleAdapter.ViewHolderSchedule> {

    Context context;

    AssigneBusProvider assigneBusProvider;
    BusProvider busProvider;

    RouteProvider routeProvider;
    PlaceProvider placeProvider;

    ScheduleProvider scheduleProvider;

    public ScheduleAdapter(@NonNull FirestoreRecyclerOptions<Schedule> options, Context context) {
        super(options);

        this.context =context;

        Log.d("SCHEDULE", "INICIO EL ADAPTERS");

        assigneBusProvider = new AssigneBusProvider();
        busProvider = new BusProvider();

        routeProvider = new RouteProvider();
        placeProvider = new PlaceProvider();

        scheduleProvider = new ScheduleProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderSchedule holder, int position, @NonNull Schedule schedule) {
        Log.d("SCHEDULE", "(ADAPTER) HORARIOS " + schedule.getSch_id() + " " + schedule.getSch_state() + " " + schedule.getSch_status());

        SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
        Date time = new Date(schedule.getSch_departure_time());
        String hora = dateformat.format(time);

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

        final String idAssigneBus = documentSnapshot.getString("sch_asb_id_number_disc");
        final String idRoute = documentSnapshot.getString("sch_rou_id_name");
        final String idSchedule = documentSnapshot.getString("sch_id");

        findDiscAssigneBus(idAssigneBus, holder);
        findRoutePlace(idRoute, holder);

        holder.txt_sch_state.setText(schedule.getSch_state()); // Cambiar estado
        holder.txt_sch_departure_time.setText(hora);

        changeStatusSchedule(idSchedule, holder);
    }

    private void changeStatusSchedule(String idSchedule, ViewHolderSchedule holder) {
        /*scheduleProvider.findScheduleStatus().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document: task.getResult()) {
                        Date currentDate = new Date();
                        // CAMBIAR EL ESTADO DEPENDIENDO DE LA HORA

                        Schedule schedule = document.toObject(Schedule.class);

                        long checkDate = schedule.getSch_departure_time();


                        if (checkDate >= currentDate.getTime()) {
                            holder.txt_sch_state.setText("EN LINEA...");
                        }
                        if (checkDate <= currentDate.getTime()) {
                            schedule.setSch_status(false);
                            schedule.setSch_state("PASADO...");
                            holder.txt_sch_state.setText("PASADO...");
                        }
                        if (checkDate == currentDate.getTime()) {
                            schedule.setSch_status(false);
                            schedule.setSch_state("PROXIMO...");
                            holder.txt_sch_state.setText("PROXIMO...");
                        }

                        saveChangeStatusSchedule(schedule);
                    }
                }
            }
        });*/
        scheduleProvider.findSchedule(idSchedule).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Date currentDate = new Date();
                    Schedule schedule = documentSnapshot.toObject(Schedule.class);

                    long checkDate = schedule.getSch_departure_time();

                    String colorGreen = "#0ABF76";
                    String colorRed = "#E81954";

                    if (checkDate >= currentDate.getTime()) {
                        long current = currentDate.getTime();
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MINUTE, + 15);
                        Date horaActual = calendar.getTime();
                        long horaLong = horaActual.getTime();

                        if (checkDate <= horaLong) {
                            holder.txt_sch_state.setText("EN LINEA...");
                            holder.txt_sch_state.setTextColor(Color.parseColor(colorGreen));
                        } else {
                            holder.txt_sch_state.setText("PROXIMO...");
                        }
                    }
                    if (checkDate <= currentDate.getTime()) {
                        //schedule.setSch_status(false);
                        long current = currentDate.getTime();
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MINUTE, -5);
                        Date horaActual = calendar.getTime();
                        long horaLong = horaActual.getTime();

                        if (checkDate >= horaLong) {
                            schedule.setSch_state("EN LINEA...");
                            holder.txt_sch_state.setText("EN LINEA...");
                            holder.txt_sch_state.setTextColor(Color.parseColor(colorGreen));

                        } else {
                            schedule.setSch_state("PASADO...");
                            holder.txt_sch_state.setText("PASADO...");
                            holder.txt_sch_state.setTextColor(Color.parseColor(colorRed));
                        }

                    }
                    if (checkDate == currentDate.getTime()) {
                        //schedule.setSch_status(false);
                        schedule.setSch_state("EN LINEA...");
                        holder.txt_sch_state.setText("EN LINEA...");
                        holder.txt_sch_state.setTextColor(Color.parseColor(colorGreen));
                    }

                    saveChangeStatusSchedule(schedule);
                }
            }
        });
    }

    private void saveChangeStatusSchedule(Schedule sch) {
        scheduleProvider.updateStatusSchedule(sch).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("SCHEDULE", "ID SCHEDULE " + sch.getSch_id() + " Estado " + sch.getSch_state() + " Status " + sch.getSch_status());
                } else  {
                    Log.d("SCHEDULE", "SE MANTIENE " + sch.getSch_id());
                }
            }
        });
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
        //U, tiliza un DocumentSnapshot
        routeProvider.getRouteByID(idRoute).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("rou_place_starting")) {
                        String idPlaceStarting = documentSnapshot.getString("rou_place_starting");
                        findDataPlace(idPlaceStarting, holder, 1);
                    }
                    if (documentSnapshot.contains("rou_place_destination")) {
                        String idPlaceDestination = documentSnapshot.getString("rou_place_destination");
                        findDataPlace(idPlaceDestination, holder, 2);
                    }
                }
            }
        });

                /*.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        });*/
    }

    private void findDataPlace(String idPlace, final ViewHolderSchedule holder, int option) {
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
        View viewHolder;

        public ViewHolderSchedule(@NonNull View v) {
            super(v);
            img_ico_state_bus = v.findViewById(R.id.id_img_ico_state_bus);
            txt_sch_number_disc_bus = v.findViewById(R.id.id_txt_sch_number_disc_bus);
            txt_sch_place_starting = v.findViewById(R.id.id_txt_sch_place_starting);
            txt_sch_place_destination = v.findViewById(R.id.id_txt_sch_place_destination);;
            txt_sch_departure_time = v.findViewById(R.id.id_txt_sch_departure_time);
            txt_sch_state = v.findViewById(R.id.id_txt_sch_status);
            viewHolder = v;
        }
    }
}
