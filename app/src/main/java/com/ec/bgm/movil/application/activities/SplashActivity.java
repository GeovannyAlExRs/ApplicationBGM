package com.ec.bgm.movil.application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ec.bgm.movil.application.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME = 4000;

    private TextView txt_slogan, txt_v1, txt_description;
    private ImageView img_logo, img_cooperativa;

    private Animation animationTop, animationBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);

        // Animations to Activity Splash...
        animationTop = AnimationUtils.loadAnimation(this, R.anim.scroll_up);
        animationBottom = AnimationUtils.loadAnimation(this, R.anim.scroll_down);

        getView();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent().setClass(SplashActivity.this, SessionModeActivity.class);
                startActivity(intent);
                finish();
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, SPLASH_TIME);
    }

    private void getView() {
        img_logo = findViewById(R.id.id_img_logo);
        img_cooperativa = findViewById(R.id.id_img_cooperativa);

        txt_slogan = findViewById(R.id.id_txt_slogan);

        txt_description = findViewById(R.id.id_txt_description);
        txt_v1 = findViewById(R.id.id_txt_v1);

        img_logo.setAnimation(animationTop);
        img_cooperativa.setAnimation(animationTop);
        txt_slogan.setAnimation(animationTop);

        txt_description.setAnimation(animationBottom);
        txt_v1.setAnimation(animationBottom);
    }
}