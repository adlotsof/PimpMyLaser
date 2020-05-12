package com.trsonn.pimpmylaser;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    AppBarConfiguration appBarConfiguration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //TODO use NavGraph here
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            //TODO make navigation fully work from all places.
            //TODO disable see_last when in listFragment
            //TODO make saved data look nice
            //DONE make shure sensors work when phoone is turned
            //TODO include measurements in navigation
            //TODO finished


            case R.id.action_see_last:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_measurementFragment);
                return true;
            case R.id.action_measure:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_SecondFragment);
                return true;
            case R.id.action_delete_all:
                SharedPreferences preferences = getSharedPreferences(Measurement.PREFSFILE, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                return true;
            case R.id.action_instructions:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_FirstFragment);
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
