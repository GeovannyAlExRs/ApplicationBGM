package com.ec.bgm.movil.application.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.model.Bus;
import com.ec.bgm.movil.application.model.Chat;
import com.ec.bgm.movil.application.model.FCMBody;
import com.ec.bgm.movil.application.model.FCMResponse;
import com.ec.bgm.movil.application.model.Place;
import com.ec.bgm.movil.application.providers.AssigneBusProvider;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.BusProvider;
import com.ec.bgm.movil.application.providers.ChatProvider;
import com.ec.bgm.movil.application.providers.CodeQRProvider;
import com.ec.bgm.movil.application.providers.NotificationProvider;
import com.ec.bgm.movil.application.providers.PlaceProvider;
import com.ec.bgm.movil.application.providers.TokenProvider;
import com.ec.bgm.movil.application.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ColectiveFragment extends Fragment implements View.OnClickListener {

    private TextView txt_number_disc_bus, txt_registration_number, txt_sch_bus_size;
    private TextView txt_first_name_driver, txt_last_name_driver, txt_phone_driver, txt_address_driver;
    private TextView txt_first_name_accompanist, txt_last_name_accompanist, txt_phone_accompanist, txt_address_accompanist;

    private TextInputEditText txt_message_place;
    private TextView txt_name_accompanist_bsd, txt_phone_accompanist_bsd, txt_employment_bsd;
    private Spinner spinner_place;

    private AppCompatButton ac_btn_select_stops, ac_btn_enviar;

    BottomSheetDialog bottomSheetDialog;

    SharedPreferences sharedPreferencesUser; // identificar el tipo de usuario
    private static final String TYPE_USER = "typeUser";

    SharedPreferences sharedPreferencesCode; // guardar el valor del code qr
    private static final String CODEQR = "code";

    SharedPreferences sharedPreferencesAssigneBus; // guardar el valor del code qr
    SharedPreferences.Editor editorAssigneBus;
    private static final String ASSGINEBUS = "asbus";

    CodeQRProvider codeQRProvider;
    AssigneBusProvider assigneBusProvider;
    BusProvider busProvider;
    UsersProvider usersProvider;

    PlaceProvider placeProvider;

    NotificationProvider notificationProvider;
    TokenProvider tokenProvider;

    ChatProvider chatProvider;
    AuthFirebaseProvider authFirebaseProvider;

    String messagePlace;
    String itemPlaceName;
    String idPlace;

    String idUserAccompanist;
    String nameUserAccompanist;
    String phoneAccompanist;

    public ColectiveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_colective, container, false);

        getViewId(view);

        sharedPreferencesCode = ColectiveFragment.this.getActivity().getSharedPreferences(CODEQR, Context.MODE_PRIVATE);
        String idCodeQR = sharedPreferencesCode.getString("qr", "");

        sharedPreferencesUser = ColectiveFragment.this.getActivity().getSharedPreferences(TYPE_USER, Context.MODE_PRIVATE);
        String selectUser = sharedPreferencesUser.getString("user", "");


        sharedPreferencesAssigneBus = ColectiveFragment.this.getActivity().getSharedPreferences(ASSGINEBUS, Context.MODE_PRIVATE);
        editorAssigneBus = sharedPreferencesAssigneBus.edit();

        // Verifica el tipo de usuario y oculta el elemento
        if (selectUser == "empleado") {
            Toast.makeText(ColectiveFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();
            ac_btn_select_stops.setVisibility(view.GONE);
        } else {
            Toast.makeText(ColectiveFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();
        }

        codeQRProvider = new CodeQRProvider();
        assigneBusProvider = new AssigneBusProvider();
        busProvider = new BusProvider();
        usersProvider = new UsersProvider();

        placeProvider = new PlaceProvider();

        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();

        chatProvider = new ChatProvider();
        authFirebaseProvider = new AuthFirebaseProvider();

        findDataQR(idCodeQR);

        return view;
    }

    private void findDataQR(String idQR) {
        codeQRProvider.readCodeQRByID(idQR).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("gqr_asb_bus_id")) {
                        String idAssigneBus = documentSnapshot.getString("gqr_asb_bus_id");
                        editorAssigneBus.putString("asbID", idAssigneBus);
                        editorAssigneBus.apply();
                        Log.d("ENTRO", "RECUPERO ID DE ASSIGNE BUS  " + idAssigneBus);
                        findDataAssigneBus(idAssigneBus);
                    }

                }
            }
        });
    }

    private void findDataAssigneBus(String id) {
        assigneBusProvider.getAsigneBus(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("asb_bus_id")) {
                        String idBus = documentSnapshot.getString("asb_bus_id");
                        findDataBus(idBus);

                        String idUserDriver = documentSnapshot.getString("asb_driver_id");
                        finDataUser(idUserDriver, 1);

                        String idUseraccompanist = documentSnapshot.getString("asb_accompanist_id");
                        finDataUser(idUseraccompanist, 2);

                    }

                }
            }
        });
    }

    private void findDataBus(String id) {
        Log.d("ENTRO", "(BUS) BUSCAR POR ID " + id);
        busProvider.getBusByID(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("ENTRO", "entro a verificar " + id);
                if (documentSnapshot.exists()) {
                    Bus bus = documentSnapshot.toObject(Bus.class);

                    Log.d("ENTRO", "DATOS   pasados al objeto BUS " + bus.getBus_registration_number() + " " + bus.getBus_number_disc() + " " + bus.getBus_size());

                    long disco = documentSnapshot.getLong("bus_number_disc");
                    String placa = documentSnapshot.getString("bus_registration_number");
                    long capacidad = documentSnapshot.getLong("bus_size");

                    txt_number_disc_bus.setText(String.valueOf(disco));
                    txt_registration_number.setText(placa);
                    txt_sch_bus_size.setText(String.valueOf(capacidad));

                }
            }
        });
    }

    private void finDataUser(String id, int option) {
        Log.d("ENTRO", "(USER) BUSCAR POR ID " + id);
        usersProvider.getUserByID(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("ENTRO", "(USER) Entro a verificar " + id);
                if (documentSnapshot.exists()) {
                    String idUser = documentSnapshot.getString("use_id");
                    String first_name = documentSnapshot.getString("use_first_name");
                    String last_name = documentSnapshot.getString("use_last_name");
                    String phone = documentSnapshot.getString("use_phone");
                    String address = documentSnapshot.getString("use_address");

                    if (option == 1) {
                        txt_first_name_driver.setText(first_name);
                        txt_last_name_driver.setText(last_name);
                        txt_phone_driver.setText(phone);
                        txt_address_driver.setText(address);
                    } else {
                        txt_first_name_accompanist.setText(first_name);
                        txt_last_name_accompanist.setText(last_name);
                        txt_phone_accompanist.setText(phone);
                        txt_address_accompanist.setText(address);

                        // Data of User Accompanist for Botton Sheet Dialog
                        idUserAccompanist = idUser;
                        nameUserAccompanist = first_name + " " + last_name;
                        phoneAccompanist = phone;
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.id_btn_select_stops:
                Toast.makeText(ColectiveFragment.this.getActivity(), "Muestra Ahora...", Toast.LENGTH_SHORT).show();
                gotoModalPlace();
                break;
            case R.id.id_btn_enviar:
                Toast.makeText(ColectiveFragment.this.getActivity(), "Mensaje enviado...", Toast.LENGTH_SHORT).show();

                sendNotification(itemPlaceName + " " + messagePlace); // envia la notificacion
                sendChat(); // crea el chat
                bottomSheetDialog.dismiss();
                break;
        }
    }

    private void sendChat() {
        Chat chat = new Chat();

        chat.setIdUser(idUserAccompanist); // ID del usuario receptor
        chat.setIdUserGuest(authFirebaseProvider.getUidFirebase()); //ID Usuario emisor
        chat.setIdPlace(idPlace);
        chat.setWritting(false);
        chat.setTimestamp(new Date().getTime());

        chatProvider.create(chat);
    }

    private void gotoModalPlace() {
        bottomSheetDialog = new BottomSheetDialog(ColectiveFragment.this.getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.dialog_place_stops);
        bottomSheetDialog.setCanceledOnTouchOutside(false);


        ac_btn_enviar = bottomSheetDialog.findViewById(R.id.id_btn_enviar);
        ac_btn_enviar.setOnClickListener(this);

        txt_message_place = bottomSheetDialog.findViewById(R.id.id_txt_message_place);

        txt_employment_bsd = bottomSheetDialog.findViewById(R.id.id_txt_employment_bsd);
        txt_name_accompanist_bsd = bottomSheetDialog.findViewById(R.id.id_txt_name_accompanist_bsd);
        txt_phone_accompanist_bsd = bottomSheetDialog.findViewById(R.id.id_txt_phone_accompanist_bsd);

        spinner_place = bottomSheetDialog.findViewById(R.id.id_spinner_place);

        txt_employment_bsd.setText(idUserAccompanist);
        txt_name_accompanist_bsd.setText(nameUserAccompanist);
        txt_phone_accompanist_bsd.setText(phoneAccompanist);
        messagePlace = txt_message_place.getText().toString();

        getDataPlaceSpinner();


        bottomSheetDialog.show();
    }

    private void sendNotification(String message) {
        if (idUserAccompanist == null) {
            return;
        }
        tokenProvider.getToken(idUserAccompanist).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "Notificacion de paradas");
                        data.put("body", message);

                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        notificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body()!=null) {
                                    if (response.body().getSuccess() == 1) {
                                        Toast.makeText(ColectiveFragment.this.getActivity(), "Se envio la notificacion", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ColectiveFragment.this.getActivity(), "La notificacion no se envio", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ColectiveFragment.this.getActivity(), "Error al enviar la notificacion", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    } else {
                        Toast.makeText(ColectiveFragment.this.getActivity(), "El token del usuario no existe", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    private void getDataPlaceSpinner() {
        placeProvider.findAllPlace().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final ArrayList<String> placeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String placeName = document.getString("pla_name");
                        placeList.add(placeName);
                    }
                    placeSpinner(placeList);
                }
            }
        });
    }

    private void placeSpinner(ArrayList<String> placeList) {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, placeList);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner_place.setAdapter(arrayAdapter);

        spinner_place.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemPlaceName = adapterView.getSelectedItem().toString();

                Log.d("ENTRO", "(PLACE) SPINNER Seleccionaste la " + itemPlaceName);
                findPlaceByName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void findPlaceByName() {
        placeProvider.getPlaceByName(itemPlaceName).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Place place = document.toObject(Place.class);
                        idPlace = place.getPla_id();
                    }
                }
            }
        });

    }

    private void goToView(Class activiyClass) {
        Intent intent = new Intent(ColectiveFragment.this.getActivity(), activiyClass);
        startActivity(intent);
    }

    private void getViewId(View view) {
        txt_number_disc_bus= view.findViewById(R.id.id_txt_number_disc_bus);
        txt_registration_number = view.findViewById(R.id.id_txt_registration_number);
        txt_sch_bus_size = view.findViewById(R.id.id_txt_sch_bus_size);

        txt_first_name_driver = view.findViewById(R.id.id_txt_first_name_driver);
        txt_last_name_driver = view.findViewById(R.id.id_txt_last_name_driver);
        txt_phone_driver = view.findViewById(R.id.id_txt_phone_driver);
        txt_address_driver = view.findViewById(R.id.id_txt_address_driver);

        txt_first_name_accompanist = view.findViewById(R.id.id_txt_first_name_accompanist);
        txt_last_name_accompanist = view.findViewById(R.id.id_txt_last_name_accompanist);
        txt_phone_accompanist = view.findViewById(R.id.id_txt_phone_accompanist);
        txt_address_accompanist = view.findViewById(R.id.id_txt_address_accompanist);

        ac_btn_select_stops = view.findViewById(R.id.id_btn_select_stops);
        ac_btn_select_stops.setOnClickListener(this);
    }
}