package com.ec.bgm.movil.application.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.activities.SessionModeActivity;
import com.ec.bgm.movil.application.adapters.ScheduleAdapter;
import com.ec.bgm.movil.application.model.Schedule;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.RouteProvider;
import com.ec.bgm.movil.application.providers.ScheduleProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class ScheduleFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerview_schedule;
    private Spinner spinner_schedule;

    AuthFirebaseProvider authFirebaseProvider;
    RouteProvider routeProvider;
    ScheduleProvider scheduleProvider;
    ScheduleAdapter scheduleAdapter;

    String scheduleID;

    Toolbar toolbar;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        showViewToolbar(view, "BusGeoMap | Horarios", false);
        getViewId(view);

        routeProvider = new RouteProvider();
        scheduleProvider = new ScheduleProvider();

        //getDataRouteSpinner();
        spinner_schedule.setVisibility(view.GONE);

        return view;
    }

    private void showViewToolbar(View view, String title, boolean upbtn) {
        toolbar = view.findViewById(R.id.id_toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(upbtn);
    }

/*
    private void getDataRouteSpinner() {
        routeProvider.findAllRoute().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final ArrayList<String> routeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String routeName = document.getString("rou_name");

                        Log.d("ENTRO", "RUTAS " + document.getId() + " " + routeName);
                        routeList.add(routeName);
                    }
                    routeSpinner(routeList);
                } else {
                    Log.d("ENTRO", "NO HAY RUTAS", task.getException());
                }
            }
        });
    }

    private void routeSpinner(ArrayList<String> routeList) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, routeList);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner_schedule.setAdapter(arrayAdapter);

        spinner_schedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemRouteName = adapterView.getSelectedItem().toString();

                Log.d("ENTRO", "(RUTA) SPINNER Seleccionaste la " + itemRouteName);
                getRouteData(itemRouteName);   
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getRouteData(String routeName) {
        routeProvider.findRouteByName(routeName).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Route route = document.toObject(Route.class);
                        getDataSchedule(route.getRou_id());
                        Log.d("ENTRO", "(RUTA) SELECCION ID " + route.getRou_id() + " " + route.getRou_name());
                    }
                }
            }
        });
    }

    private void getDataSchedule(String rou_id) {
        scheduleProvider.findScheduleByRouteID(rou_id).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Schedule sch = document.toObject(Schedule.class);
                            scheduleID = sch.getSch_id();
                            Log.d("ENTRO", "(HORARIO) CAMBIO A SELECCION " + scheduleID + " " + sch.getSch_state());
                        }
                    }
                } else {
                    Log.d("ENTRO", "NO HAY HORARIOS EN LISTA", task.getException());
                }
            }
        });
    }
*/


    private void getAllScheduleRecyclerView() {
        Query query = scheduleProvider.findAllSchedule();
        Log.d("SCHEDULE", "Entro al onStart QUERY " + query.get());

        FirestoreRecyclerOptions<Schedule> options = new  FirestoreRecyclerOptions.Builder<Schedule>()
                .setQuery(query, Schedule.class)
                .build();

        Log.d("SCHEDULE", "FirestoreRecyclerview OPTIONS " +options.toString());

        scheduleAdapter = new ScheduleAdapter(options, ScheduleFragment.this.getActivity());
        scheduleAdapter.notifyDataSetChanged();
        recyclerview_schedule.setAdapter(scheduleAdapter);
        //scheduleAdapter.notifyDataSetChanged();
        scheduleAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllScheduleRecyclerView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_action_logout:
                authFirebaseProvider.logout();
                goToView(SessionModeActivity.class, true);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        scheduleAdapter.stopListening();
    }

    public void goToView(Class activiyClass, boolean band) {
        Intent intent = new Intent(ScheduleFragment.this.getActivity(), activiyClass);

        if (band == false) {
            startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void getViewId(View view) {
        recyclerview_schedule = view.findViewById(R.id.id_recyclerview_schedule);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(false);
        recyclerview_schedule.setLayoutManager(linearLayoutManager);

        spinner_schedule = view.findViewById(R.id.id_spinner_schedule);
    }
}