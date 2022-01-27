package com.ec.bgm.movil.application.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.model.UserGuest;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.CodeQRProvider;
import com.ec.bgm.movil.application.providers.UserGuestProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScannQrActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView circleBack;
    private AppCompatButton ac_btn_scannerQR;
    private TextInputEditText txt_name, txt_email;

    AuthFirebaseProvider authFirebaseProvider;
    UserGuestProvider userGuestProvider;
    CodeQRProvider codeQRProvider;

    SharedPreferences sharedPreferencesCode; // guardar el valor del code qr
    SharedPreferences.Editor editorQR;
    private static final String CODEQR = "code";

    String id_rol = "BGM_ROLEGsfzzkV61H";
    String email;
    String username;
    String password = "invitado2022";
    String codigoQR = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scann_qr);

        getViewId();

        authFirebaseProvider = new AuthFirebaseProvider();
        userGuestProvider = new UserGuestProvider();
        codeQRProvider = new CodeQRProvider();

        sharedPreferencesCode = getApplicationContext().getSharedPreferences(CODEQR, MODE_PRIVATE);
        editorQR = sharedPreferencesCode.edit();
    }

    private void getViewId() {
        circleBack = findViewById(R.id.id_circleBack);
        circleBack.setOnClickListener(this);

        txt_email = findViewById(R.id.id_txt_email);
        txt_name = findViewById(R.id.id_txt_name);


        ac_btn_scannerQR = findViewById(R.id.id_btn_scannerQR);
        ac_btn_scannerQR.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_circleBack:
                finish();
                break;
            case R.id.id_btn_scannerQR:
                goAuthUser();
                break;
        }
    }

    private void goAuthUser() {
        email = txt_email.getText().toString();
        username = txt_name.getText().toString();

        if (!email.isEmpty() || !username.isEmpty()) {
            if (isEmailValid(email)) {
                authFirebaseProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ScannQrActivity.this, "Usuario *" + email + "* Correcto", Toast.LENGTH_SHORT).show();

                            gotoViewScanner();
                        } else {
                            Toast.makeText(ScannQrActivity.this, "Crear usuario *" + email + "*", Toast.LENGTH_SHORT).show();
                            gotoRegisterUser();
                        }
                    }
                });
            } else {
                Toast.makeText(ScannQrActivity.this, "Email no es valido", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Ingrese email y nombre", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoRegisterUser() {
        authFirebaseProvider.register(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String iud = authFirebaseProvider.getUidFirebase();

                    UserGuest user = new UserGuest();

                    user.setId_userGuest(iud);
                    user.setId_rol(id_rol);
                    user.setUsername(username);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setRegister_date(new Date().getTime());

                    saveUserGuest(user);
                }
            }
        });
    }

    private void saveUserGuest(UserGuest user) {
        userGuestProvider.createUserGuest(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ScannQrActivity.this, "Usuario *" + email + "* Registrado", Toast.LENGTH_SHORT).show();
                    gotoViewScanner();
                } else {
                    Toast.makeText(ScannQrActivity.this, "El usuario no se registro correctamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void gotoViewScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanner Code QR");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureQRActivity.class);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cerro el lector QR", Toast.LENGTH_SHORT).show();
            } else {
                checkCodeQRExist(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkCodeQRExist(String idQR) {
        codeQRProvider.readCodeQRByID(idQR).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("gqr_code")) {
                        codigoQR = documentSnapshot.getString("gqr_code");
                        editorQR.putString("qr", codigoQR);
                        editorQR.apply();
                        Toast.makeText(ScannQrActivity.this, "Codigo QR " + codigoQR + "", Toast.LENGTH_SHORT).show();
                        goToView(MainActivity.class);
                    }

                }
            }
        });
    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void goToView(Class activiyClass) {
        Intent intent = new Intent(this, activiyClass);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}