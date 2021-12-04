package com.example.tracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tracking.viewmodel.TripViewModel;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static Context context;
    private MenuItem home;
    private boolean showHomeButton;
    private TripViewModel tripViewModel;
    private NavController navController;
    private final String PERSONFILE = "personFile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

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

        tripViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        tripViewModel.deleteCurrentLocationsExceptLast();
        tripViewModel.deleteTripsToBeRegistered();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.toolbar_menu, menu);
         home = menu.findItem(R.id.welcomeFragment);
        File file = new File(PERSONFILE);
        if(file.exists()){
            home.setVisible(true);
            //Toast.makeText(this, "File exists.", Toast.LENGTH_SHORT).show();
        } else {
            home.setVisible(false);
            //Toast.makeText(this, "File does not exist.", Toast.LENGTH_SHORT).show();
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout_toolbar || id == R.id.info_toolbar ) {
            if (id == R.id.logout_toolbar)
                Toast.makeText(this, "You are exiting", Toast.LENGTH_SHORT).show();
            if (id == R.id.info_toolbar)
                navController.navigate(R.id.newTripFragment);
        } else {
            navController.navigate(R.id.startFragment);
        }
        return super.onOptionsItemSelected(item);
    }


}