package com.ec.bgm.movil.application.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ColectiveFragment extends Fragment implements View.OnClickListener {

    private TextView txt_number_disc_bus, txt_registration_number, txt_sch_bus_size;
    private TextView txt_first_name_driver, txt_last_name_driver, txt_phone_driver, txt_address_driver;
    private TextView txt_first_name_accompanist, txt_last_name_accompanist, txt_phone_accompanist, txt_address_accompanist;

    private AppCompatButton ac_btn_select_stops;

    BottomSheetDialog bottomSheetDialog;

    public ColectiveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_colective, container, false);
        getViewId(view);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.id_btn_select_stops:
                Toast.makeText(ColectiveFragment.this.getActivity(), "Muestra Ahora...", Toast.LENGTH_SHORT).show();
                //goToView(PlaceStopsActivity.class);
                gotoModalPlace();
                break;
            case R.id.id_btn_enviar:
                Toast.makeText(ColectiveFragment.this.getActivity(), "Mensaje enviado...", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
                break;
        }
    }

    private void gotoModalPlace() {
        bottomSheetDialog = new BottomSheetDialog(ColectiveFragment.this.getActivity(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.dialog_place_stops);
        bottomSheetDialog.setCanceledOnTouchOutside(false);

        AppCompatButton ac_btn_enviar = bottomSheetDialog.findViewById(R.id.id_btn_enviar);
        ac_btn_enviar.setOnClickListener(this);

        bottomSheetDialog.show();
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

        //BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ColectiveFragment.this.getActivity(), R.style.BottomSheetDialogTheme);
        //view = LayoutInflater.from(ColectiveFragment.this.getActivity()).inflate(R.layout.);
    }
}