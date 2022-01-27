package com.ec.bgm.movil.application.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ec.bgm.movil.application.R;
import com.ec.bgm.movil.application.fragments.ColectiveFragment;
import com.ec.bgm.movil.application.fragments.MapsFragment;
import com.ec.bgm.movil.application.fragments.NotificationFragment;
import com.ec.bgm.movil.application.fragments.ScheduleFragment;
import com.ec.bgm.movil.application.includes.MyToolbar;
import com.ec.bgm.movil.application.providers.AuthFirebaseProvider;
import com.ec.bgm.movil.application.providers.TokenProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private Toolbar toolbar;

    TokenProvider tokenProvider;
    AuthFirebaseProvider authFirebaseProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyToolbar.showView(this, "BusGeoMap", false);

        getViewId();

        tokenProvider = new TokenProvider();
        authFirebaseProvider = new AuthFirebaseProvider();

        viewFragment(new MapsFragment());

        String idUser = authFirebaseProvider.getUidFirebase();
        Log.d("ENTRO", "ID DEL USUARIO LOGUEADO " + idUser);
        createToken(idUser);
    }

    private void getViewId() {
        bottomNavigationView = findViewById(R.id.id_bottom_navigation_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.id_action_logout); {
            //disconnect();
            //firebaseAuth.logout();
            goToView(SessionModeActivity.class, 2);
            Toast.makeText(this, "Cerrar Sesion", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
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

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.id_item_navigation_maps:
                            viewFragment(new MapsFragment());
                            return true;
                        case R.id.id_item_navigation_shedule:
                            viewFragment(new ScheduleFragment());
                            return true;
                        case R.id.id_item_navigation_colective:
                            viewFragment(new ColectiveFragment());
                            return true;
                        case R.id.id_item_navigation_notifications:
                            viewFragment(new NotificationFragment());
                            return true;
                    }
                    return true;
                }
            };

    public void viewFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void createToken(String id) {
        tokenProvider.create(id);
    }
}