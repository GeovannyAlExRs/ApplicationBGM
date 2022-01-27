package com.ec.bgm.movil.application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText txt_email, txt_password;

    CircleImageView circleBack;
    private AppCompatButton ac_btn_login;

    private static final int OPTIONVIEW1 = 1;
    private static final int OPTIONVIEW2 = 2;

    AuthFirebaseProvider authFirebaseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getViewId();

        authFirebaseProvider = new AuthFirebaseProvider();
    }

    private void getViewId() {
        txt_email = findViewById(R.id.id_txt_email);
        txt_password = findViewById(R.id.id_txt_password);

        circleBack = findViewById(R.id.id_circleBack);
        circleBack.setOnClickListener(this);

        ac_btn_login = findViewById(R.id.id_btn_login);
        ac_btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_circleBack:
                finish();
                break;
            case R.id.id_btn_login:
                goToLogin();
                break;
        }
    }

    private void goToLogin() {
        String email = txt_email.getText().toString();
        String clave = txt_password.getText().toString();
        Log.d("ENTRADA", "email: " + email + ", password: " + clave);

        authFirebaseProvider.login(email, clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "INGRESO CORRECTO", Toast.LENGTH_SHORT).show();
                    goToView(MainActivity.class, OPTIONVIEW1);
                } else {
                    Toast.makeText(LoginActivity.this, "El Email o la clave son incorrectas", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void goToView(Class activiyClass, int option) {
        Intent intent = new Intent(this, activiyClass);

        if (option == 1) {
            startActivity(intent);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}