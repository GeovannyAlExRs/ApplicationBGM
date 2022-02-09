package com.ec.bgm.movil.application.providers;

import com.ec.bgm.movil.application.model.Schedule;
import com.ec.bgm.movil.application.model.UserGuest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ScheduleProvider {

    CollectionReference collectionReference;

    public ScheduleProvider () {
        collectionReference = FirebaseFirestore.getInstance().collection("Schedule");
    }

    public Task<QuerySnapshot> findScheduleByRouteID(String routeID) {
        return collectionReference.whereEqualTo("sch_rou_id_name", routeID).whereEqualTo("sch_status", true).get();
    }

    public Task<DocumentSnapshot> findSchedule(String idSchedule) {
        return collectionReference.document(idSchedule).get();
    }

    public Query getScheduleByID(String idSchedule) {
        return collectionReference.whereEqualTo("sch_id", idSchedule).orderBy("sch_departure_time", Query.Direction.DESCENDING);
    }

    public Query findAllSchedule() {
        return collectionReference.whereEqualTo("sch_status", true).orderBy("sch_departure_time", Query.Direction.DESCENDING); //REVISAR BIEN LA CONSULTA PARA QUE MUESTRE SOLO LOS DE ESTADO TRUE
    }
    /*public Task<QuerySnapshot> findScheduleStatus() {
        return collectionReference.whereEqualTo("sch_status", true).orderBy("sch_departure_time", Query.Direction.DESCENDING).get();
    }*/

    public Task<Void> updateStatusSchedule(Schedule schedule) {
        return collectionReference.document(schedule.getSch_id()).set(schedule);
    }
}
