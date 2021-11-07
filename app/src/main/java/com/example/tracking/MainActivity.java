package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    static Context context;
    private MenuItem home;
    private boolean showHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // *** NavigationUI & toolbar:
        // 1. Konfiguerer top app bar (sørger for tilbakeknapp, hamburgermeny osv.)
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        // 2. Konfigurerer toolbar (må være definert i activity_main.xml):
        //Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        //setSupportActionBar(mainToolbar);
        // 3. Kopler til Toolbar:
        //NavigationUI.setupWithNavController(mainToolbar, navController, appBarConfiguration);


        /*navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

            }
        });*/

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        /*logout = menu.findItem(R.id.logout_toolbar);
        settings = menu.findItem(R.id.settings_toolbar);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null){
            logout.setVisible(false);
            settings.setVisible(false);
        } else {
            logout.setVisible(true);
            settings.setVisible(true);
        }*/
        //VISER/SKJULER HOME-KNAPP
        //home = menu.findItem(R.id.welcomeFragment);
        //home.setVisible(showHomeButton);
        return true;
    }

}