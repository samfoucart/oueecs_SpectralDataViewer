package android.example.ohiouniversityspectrometerdatacollection;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    // Debug
    private static final String TAG = "MainActivity";

    // Layout Views
    private BottomNavigationView mBottomNav;
    private ActionBar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = getSupportActionBar();

        // Setup Listener for bottom navigation bar
        mBottomNav = findViewById(R.id.bottom_navigation);
        mBottomNav.setOnNavigationItemSelectedListener(navListener);
        // Open the Spectrometer Settings Fragment first
        mBottomNav.setSelectedItemId(R.id.nav_user_input);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SpectrometerSettingsFragment()).commit();
        // Set Toolbar title
        mToolBar.setTitle(R.string.toolbar_spectrometer_settings);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth_connection_menu:
                DialogFragment dialog = new BluetoothDevicesDialogFragment();
                dialog.show(getSupportFragmentManager(), "BluetoothDevices");
                return true;
        }
        return false;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment;

                    // Get the selected Fragment
                    switch (menuItem.getItemId()) {
                        case R.id.nav_files:
                            mToolBar.setTitle(R.string.saved_files_title);
                            selectedFragment = new FilesFragment();
                            loadFragment(selectedFragment);
                            return true;
                        case R.id.nav_user_input:
                            mToolBar.setTitle(R.string.toolbar_spectrometer_settings);
                            selectedFragment = new SpectrometerSettingsFragment();
                            loadFragment(selectedFragment);
                            return true;
                        case R.id.nav_graph:
                            mToolBar.setTitle(R.string.graph_title);
                            selectedFragment = new GraphFragment();
                            loadFragment(selectedFragment);
                            return true;
                    }

                    return false;
                }
            };

    /**
     * Replace and commit fragment
     * @param fragment a non null fragment to be displayed
     */
    private void loadFragment(@NonNull Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        // Material Guidelines used to say not to add these to back stack, but Youtube adds to back
        // stack, so they removed that, and I'm not sure which to pick.
        // fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
