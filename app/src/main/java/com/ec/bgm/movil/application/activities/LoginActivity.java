package com.ec.bgm.movil.application.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.google.android.material.textfield.TextInputEditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText txt_email, txt_password;

    CircleImageView circleBack;
    private AppCompatButton ac_btn_login;

    private static final int OPTIONVIEW1 = 1;
    private static final int OPTIONVIEW2 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getViewId();
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
                Toast.makeText(this, "Proximamente...", Toast.LENGTH_SHORT).show();
                goToLogin();
                goToView(MainActivity.class, OPTIONVIEW1);
                break;
        }
    }

    private void goToLogin() {
        String email = txt_email.getText().toString();
        String clave = txt_password.getText().toString();
        Log.d("ENTRADA", "email: " + email + ", password: " + clave);

        /*firebaseAuth.login(email, clave).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "INGRESO CORRECTO", Toast.LENGTH_SHORT).show();
                    goToView(MainActivity.class, OPTIONVIEW1);
                } else {
                    Toast.makeText(LoginActivity.this, "El Email o la clave son incorrectas", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
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