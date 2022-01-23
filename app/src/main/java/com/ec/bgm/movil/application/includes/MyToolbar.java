package com.ec.bgm.movil.application.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ec.bgm.movil.application.R;

public class MyToolbar {

    public static void showView(AppCompatActivity activity, String title, boolean upbtn) {
        Toolbar toolbar = activity.findViewById(R.id.id_toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upbtn);
    }
}
