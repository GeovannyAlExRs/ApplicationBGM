package com.ec.bgm.movil.application.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.activities.SessionModeActivity;
import com.ec.bgm.movil.application.model.Assignes_Bus;
import com.ec.bgm.movil.application.model.Bus;
import com.ec.bgm.movil.application.model.Chat;
import com.ec.bgm.movil.application.model.FCMBody;
import com.ec.bgm.movil.application.model.FCMResponse;
import com.ec.bgm.movil.application.model.Message;
import com.ec.bgm.movil.application.model.Place;
import com.ec.bgm.movil.application.model.Stops;
import com.ec.bgm.movil.application.providers.AssigneBusProvider;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.BusProvider;
import com.ec.bgm.movil.application.providers.ChatProvider;
import com.ec.bgm.movil.application.providers.CodeQRProvider;
import com.ec.bgm.movil.application.providers.MessageProvider;
import com.ec.bgm.movil.application.providers.NotificationProvider;
import com.ec.bgm.movil.application.providers.PlaceProvider;
import com.ec.bgm.movil.application.providers.StopsProvider;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ColectiveFragment extends Fragment implements View.OnClickListener {

    private TextView txt_number_disc_bus, txt_registration_number, txt_sch_bus_size, txt_alerta;
    private TextView txt_first_name_driver, txt_last_name_driver, txt_phone_driver, txt_address_driver;
    private TextView txt_first_name_accompanist, txt_last_name_accompanist, txt_phone_accompanist, txt_address_accompanist;

    private TextView txt_name_accompanist_bsd, txt_employment_bsd;
    private Spinner spinner_place;

    private AppCompatButton ac_btn_select_stops;
    private ImageView img_btn_send;
    private EditText text_send;

    Toolbar toolbar;

    BottomSheetDialog bottomSheetDialog;

    SharedPreferences sharedPreferencesUser; // identificar el tipo de usuario
    private static final String TYPE_USER = "typeUser";

    SharedPreferences sharedPreferencesCode; // guardar el valor del code qr
    private static final String CODEQR = "code";

    SharedPreferences sharedPreferencesAssigneBus; // guardar el valor del code qr
    SharedPreferences.Editor editorAssigneBus;
    private static final String ASSGINEBUS = "asbus";

    SharedPreferences sharedPreferencesUserAccompanist; // Guardar ID USURIO OFICIAL
    SharedPreferences.Editor editorAccompanist;
    private static final String USER_ACCOMPANIST = "userAccompanist";

    CodeQRProvider codeQRProvider;
    AssigneBusProvider assigneBusProvider;
    BusProvider busProvider;
    UsersProvider usersProvider;

    PlaceProvider placeProvider;

    NotificationProvider notificationProvider;
    TokenProvider tokenProvider;

    ChatProvider chatProvider;
    AuthFirebaseProvider authFirebaseProvider;

    MessageProvider messageProvider;
    StopsProvider stopsProvider;
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
        showViewToolbar(view, "BusGeoMap | Bus", false);
        getViewId(view);

        sharedPreferencesAssigneBus = ColectiveFragment.this.getActivity().getSharedPreferences(ASSGINEBUS, Context.MODE_PRIVATE);
        editorAssigneBus = sharedPreferencesAssigneBus.edit();

        sharedPreferencesUserAccompanist = ColectiveFragment.this.getActivity().getSharedPreferences(USER_ACCOMPANIST, Context.MODE_PRIVATE);
        editorAccompanist = sharedPreferencesUserAccompanist.edit();

        codeQRProvider = new CodeQRProvider();
        assigneBusProvider = new AssigneBusProvider();
        busProvider = new BusProvider();
        usersProvider = new UsersProvider();

        messageProvider = new MessageProvider();
        stopsProvider = new StopsProvider();
        placeProvider = new PlaceProvider();

        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();

        chatProvider = new ChatProvider();
        authFirebaseProvider = new AuthFirebaseProvider();

        validationTypeUser(view);

        return view;
    }

    private void validationTypeUser(View view) {
        sharedPreferencesUser = ColectiveFragment.this.getActivity().getSharedPreferences(TYPE_USER, Context.MODE_PRIVATE);
        String selectUser = sharedPreferencesUser.getString("user", "");

        if (selectUser.equals("empleado")) {
            Toast.makeText(ColectiveFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();
            ac_btn_select_stops.setVisibility(view.GONE);

            ac_btn_select_stops.setVisibility(view.GONE);

            txt_phone_driver.setVisibility(View.VISIBLE);
            txt_phone_accompanist.setVisibility(view.VISIBLE);

            txt_address_driver.setVisibility(view.VISIBLE);
            txt_address_accompanist.setVisibility(view.VISIBLE);
            String id = authFirebaseProvider.getUidFirebase();

            findDataAssigneBusByUser(id);

        }
        if (selectUser.equals("invitado")) {
            Toast.makeText(ColectiveFragment.this.getActivity(), "Tipo de usuario " + selectUser, Toast.LENGTH_SHORT).show();

            sharedPreferencesCode = ColectiveFragment.this.getActivity().getSharedPreferences(CODEQR, Context.MODE_PRIVATE);
            String idCodeQR = sharedPreferencesCode.getString("qr", "");
            //OJO VERIFICAR
            Log.d("ENTRO", "INICIO DEL MODULO - ID DEL CODE QR " + idCodeQR);
            if (!idCodeQR.isEmpty()) {
                Log.d("ENTRO", "ENTRO A LA CONDICION " + idCodeQR);
                findDataQR(idCodeQR);
            }

            ac_btn_select_stops.setVisibility(view.VISIBLE);

            txt_phone_driver.setVisibility(view.GONE);
            txt_phone_accompanist.setVisibility(view.GONE);

            txt_address_driver.setVisibility(view.GONE);
            txt_address_accompanist.setVisibility(view.GONE);
        }
    }


    private void findDataQR(String idQR) {
        codeQRProvider.readCodeQRByID(idQR).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("gqr_asb_bus_id")) {
                        String idAssigneBus = documentSnapshot.getString("gqr_asb_bus_id");
                        //editorAssigneBus.putString("asbID", idAssigneBus);
                        //editorAssigneBus.apply();
                        Log.d("ENTRO", "CODIGO QR " + idQR + " RECUPERO ID DE ASSIGNE BUS  " + idAssigneBus);
                        findDataAssigneBus(idAssigneBus);
                    }

                }
            }
        });
    }

    // Buscar Documento ASSIGNE BUS por el ID del Documento
    private void findDataAssigneBus(String id) {
        Log.d("ENTRO", "ID ASSIGNE BUS " + id);
        assigneBusProvider.getAsigneBus(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Assignes_Bus assbus = documentSnapshot.toObject(Assignes_Bus.class);
                    findDataBus(assbus.getAsb_bus_id());

                    finDataUser(assbus.getAsb_driver_id(), 1);
                    finDataUser(assbus.getAsb_accompanist_id(), 2);
                }
            }
        });
    }

    // Buscar Documento ASSIGNE BUS por el ID del Oficial
    private void findDataAssigneBusByUser(String id) {
        Log.d("ENTRO", "ID Usuario empleado " + id);
        assigneBusProvider.getAsigneBusByUser(id).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Assignes_Bus assbus = document.toObject(Assignes_Bus.class);
                            findDataBus(assbus.getAsb_bus_id());

                            finDataUser(assbus.getAsb_driver_id(), 1);
                            finDataUser(assbus.getAsb_accompanist_id(), 2);
                        }
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
                        editorAccompanist.putString("ID_ACCOMPANIST", idUserAccompanist);
                        editorAccompanist.apply();

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
                gotoModalPlace();
                break;
            case R.id.id_Img_btn_send:
                sendNotification(itemPlaceName); // envia la notificacion
                //checkChatExits();
                String idUserGuest = authFirebaseProvider.getUidFirebase();
                sendChat(idUserGuest); // crea el chat

                bottomSheetDialog.dismiss();
                break;
            case R.id.id_action_logout:
                authFirebaseProvider.logout();
                goToView(SessionModeActivity.class, true);
                clearViews();
                break;
        }
    }

    private void sendChat(String idUserGuest) {

        Chat chat = new Chat();

        String id = idUserGuest+idUserAccompanist;

        chat.setIdChat(id);
        chat.setIdUser(idUserAccompanist); // ID del usuario receptor
        chat.setIdUserGuest(idUserGuest); //ID Usuario emisor
        chat.setIdPlace(idPlace);
        chat.setWritting(false);
        chat.setTimestamp(new Date().getTime());

        ArrayList<String> listIDs = new ArrayList<>();
        listIDs.add(idUserAccompanist);
        listIDs.add(idUserGuest);
        chat.setIds(listIDs);

        chatProvider.create(chat);

        //sendMessage(idUserGuest);
        messageDetail(id, idUserGuest);
    }

    private void sendMessage(final String id) {

        chatProvider.getAllChat(id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Chat chat = document.toObject(Chat.class);
                            Log.d("ENTRO", "(CHAT) Id del CHAT " + chat.getIdChat());
                            messageDetail(chat.getIdChat(), id);
                        }
                    }
                }
            }
        });
    }

    private void messageDetail(String idChat, final String id) {
        String msg = text_send.getText().toString();
        String messageText = "Lugar de destino " + itemPlaceName + " - " + msg;

        Log.d("ENTRO", "(MESSAGE) ID PLACE "+ idPlace + " Mensaje de texto " + messageText + " ID USERS " + id);

        if (!messageText.isEmpty()) {
            //saveMessage(idChat, id, messageText);
            saveStops(idChat, id, messageText);
        }
    }

    private void saveMessage(String idChat, final String id, String messageText) {
        Message message = new Message();
        message.setIdChat(idChat);
        message.setTimestamp(new  Date().getTime());
        message.setMessage(messageText);
        message.setViewed(false);

        if (authFirebaseProvider.getUidFirebase().equals(idUserAccompanist)) {
            message.setIdSender(idUserAccompanist);
            message.setIdReceiver(id);
        } else {
            message.setIdSender(id);
            message.setIdReceiver(idUserAccompanist);
        }

        messageProvider.createMessage(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ColectiveFragment.this.getActivity(), "El mensaje se envio correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ColectiveFragment.this.getActivity(), "El mensaje NO se envio correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveStops(String idChat, final String id, String messageText) {
        Stops stops = new Stops();

        stops.setIdChat(idChat);
        stops.setTimestamp(new  Date().getTime());
        stops.setMessage(messageText);
        stops.setIdPlace(idPlace);
        stops.setStatus(true);

        if (authFirebaseProvider.getUidFirebase().equals(idUserAccompanist)) {
            stops.setIdSender(idUserAccompanist);
            stops.setIdReceiver(id);
        } else {
            stops.setIdSender(id);
            stops.setIdReceiver(idUserAccompanist);
        }

        stopsProvider.createStops(stops).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ColectiveFragment.this.getActivity(), "El mensaje se envio correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ColectiveFragment.this.getActivity(), "El mensaje NO se envio correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkChatExits() {
        String idUserGuest = authFirebaseProvider.getUidFirebase();
        chatProvider.checkUsers(idUserGuest, idUserAccompanist).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                if (size == 0) {
                    Toast.makeText(ColectiveFragment.this.getActivity(), " EXISTE EL CHAT", Toast.LENGTH_SHORT).show();
                    //sendChat();
                } else {
                    Toast.makeText(ColectiveFragment.this.getActivity(), " NO EXISTE EL CHAT", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void gotoModalPlace() {
        bottomSheetDialog = new BottomSheetDialog(ColectiveFragment.this.getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.dialog_place_stops);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        img_btn_send = bottomSheetDialog.findViewById(R.id.id_Img_btn_send);
        img_btn_send.setOnClickListener(this);

        txt_alerta = bottomSheetDialog.findViewById(R.id.id_txt_alerta);
        txt_employment_bsd = bottomSheetDialog.findViewById(R.id.id_txt_employment_bsd);
        txt_name_accompanist_bsd = bottomSheetDialog.findViewById(R.id.id_txt_name_accompanist_bsd);

        spinner_place = bottomSheetDialog.findViewById(R.id.id_spinner_place);

        txt_employment_bsd.setText(idUserAccompanist);
        txt_name_accompanist_bsd.setText(nameUserAccompanist);

        text_send = bottomSheetDialog.findViewById(R.id.id_text_send);

        getDataPlaceSpinner();

        validateSelectStop();
    }

    private void validateSelectStop() {
        String idUserSession = authFirebaseProvider.getUidFirebase();
        stopsProvider.findStopsBySender(idUserSession, idUserAccompanist).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("ENTRO", "SE EJECUTO LA TAREA");
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("ENTRO", "ENTRO AL CICLO DOCUMENTO" + document);
                            if (document.exists()) {
                                Log.d("ENTRO", "DOCUMENTO EXISTE");
                                Stops stops = document.toObject(Stops.class);

                                SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH");

                                Date current = new Date(new Date().getTime());
                                final String dateCurrent = dateformat.format(current);

                                Date timestamp = new Date(stops.getTimestamp());
                                String dateStops = dateformat.format(timestamp);

                                if (dateCurrent.equals(dateStops)) {
                                    Log.d("ENTRO", "Las Fechas son iguales " + dateStops + " FECHA ACTUAL " + dateCurrent);

                                    Toast.makeText(ColectiveFragment.this.getActivity(), "YA REALIZO UNA PARADA", Toast.LENGTH_LONG).show();
                                    bottomSheetDialog.dismiss();
                                    return;
                                }
                                if (!dateCurrent.equals(dateStops)){
                                    bottomSheetDialog.show();
                                    Log.d("ENTRO", "Las Fechas NO coinciden " + dateStops + " FECHA ACTUAL " + dateCurrent);
                                }
                            } else {
                                Log.d("ENTRO", "DOCUMENTO NO EXISTE");
                                //bottomSheetDialog.show();
                            }
                        }
                    } else {
                        //bottomSheetDialog.show();
                        Log.d("ENTRO", "LA TAREA ES NULO ");
                    }
                }
            }
        });

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

                Log.d("ENTRO", "(PLACE) SPINNER Seleccionaste " + itemPlaceName);
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
                        Log.d("ENTRO", "(PLACE) ID Seleccionado " + idPlace);
                    }
                }
            }
        });

    }

    private void goToView(Class activiyClass, boolean band) {
        Intent intent = new Intent(ColectiveFragment.this.getActivity(), activiyClass);

        if (band == false) {
            startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void showViewToolbar(View view, String title, boolean upbtn) {
        toolbar = view.findViewById(R.id.id_toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(upbtn);
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

    private void clearViews() {
        editorAccompanist = null;

        txt_number_disc_bus.setText("00");
        txt_registration_number.setText("ABC-123");
        txt_sch_bus_size.setText("00");

        txt_first_name_driver.setText(" ");
        txt_last_name_driver.setText(" ");
        txt_phone_driver.setText(" ");
        txt_address_driver.setText(" ");

        txt_first_name_accompanist.setText(" ");
        txt_last_name_accompanist.setText(" ");
        txt_phone_accompanist.setText(" ");
        txt_address_accompanist.setText(" ");

        txt_alerta.setText(" ");
        txt_employment_bsd.setText(" ");
        txt_name_accompanist_bsd.setText(" ");

        txt_employment_bsd.setText(" ");
        txt_name_accompanist_bsd.setText(" ");

        ac_btn_select_stops.setVisibility(View.VISIBLE);

        ac_btn_select_stops.setVisibility(View.VISIBLE);

        txt_phone_driver.setVisibility(View.VISIBLE);
        txt_phone_accompanist.setVisibility(View.VISIBLE);

        txt_address_driver.setVisibility(View.VISIBLE);
        txt_address_accompanist.setVisibility(View.VISIBLE);

    }
}