package com.ec.bgm.movil.application.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScannQrActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView circleBack;
    private AppCompatButton ac_btn_scannerQR;
    private TextInputEditText txt_name, txt_email;

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
                //goAuthUser();
                gotoViewScanner();
                break;
        }
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
                //txt_name.setText(result.getContents());
                //checkCodeQRExist(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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