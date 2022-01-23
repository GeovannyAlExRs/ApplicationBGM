package com.ec.bgm.movil.application.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ec.bgm.movil.application.R;

public class SessionModeActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatButton ac_btn_user_employment, ac_btn_user_guest;

    SharedPreferences sharedPreferencesUser; // identificar el tipo de usuario
    SharedPreferences.Editor editor;
    private static final String TYPE_USER = "typeUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_mode);

        getViewId();

        sharedPreferencesUser = getApplicationContext().getSharedPreferences(TYPE_USER, MODE_PRIVATE);
        editor = sharedPreferencesUser.edit();
    }

    private void getViewId() {
        ac_btn_user_employment = findViewById(R.id.id_btn_user_employment);
        ac_btn_user_guest = findViewById(R.id.id_btn_user_guest);

        ac_btn_user_employment.setOnClickListener(this);
        ac_btn_user_guest.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_user_employment:
                editor.putString("user", "empleado");
                editor.apply();
                goToView(LoginActivity.class, false);
                break;
            case R.id.id_btn_user_guest:
                editor.putString("user", "invitado");
                editor.apply();
                goToView(ScannQrActivity.class, false);
                //Toast.makeText(this, "Proximamente...", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void goToView(Class activiyClass, boolean band) {
        Intent intent = new Intent(SessionModeActivity.this, activiyClass);

        if (band == false) {
            startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}