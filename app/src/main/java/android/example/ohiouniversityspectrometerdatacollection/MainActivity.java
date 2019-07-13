package android.example.ohiouniversityspectrometerdatacollection;



import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements BluetoothDevicesDialogFragment.DeviceDialogListener, SpectrometerSettingsFragment.ParametersInterface {
    private static final String TAG = "MainActivity";

    // Intent request codes
    private static final int REQUEST_ENABLE_BT = 1;

    // Layout Views
    private BottomNavigationView mBottomNav;
    private ActionBar mToolBar;
    private ProgressBar mProgressBar;

    // Member Fields
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mBluetoothService;
    private DeviceViewModel mDeviceViewModel;
    private StringBuffer mOutStringBuffer;
    private String mConnectedDevice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = getSupportActionBar();

        // Setup Listener for bottom navigation bar
        mBottomNav = findViewById(R.id.bottom_navigation);
        mProgressBar = findViewById(R.id.loading_bar);
        mBottomNav.setOnNavigationItemSelectedListener(navListener);
        // Open the Spectrometer Settings Fragment first
        mBottomNav.setSelectedItemId(R.id.nav_user_input);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new SpectrometerSettingsFragment()).commit();
        // Set Toolbar title
        mToolBar.setTitle(R.string.toolbar_spectrometer_settings);

        // Get the local bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available on this device.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        mDeviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If Bluetooth is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (mBluetoothService == null) {
            setupCommunication();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBluetoothService != null) {
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                mBluetoothService.start();
            }
        }
    }

    private void setupCommunication() {
        Log.d(TAG, "SetupCommunication()");

        // Initialize the BluetoothService to perform bluetooth connections
        mBluetoothService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");


        // Connect to device if it's selected
        if (mDeviceViewModel.getSelected() != null &&
                mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            mBluetoothService.connect(mDeviceViewModel.getSelected(), false);
            //mTestButton.setVisibility(View.VISIBLE);
            //mTestEditText.setVisibility(View.VISIBLE);
            /*
            mTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Send a message using content of the edit text widget
                    String information = mTestEditText.getText().toString();
                    sendInformation(information);
                }
            });
             */
        }

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            SpectrometerSettingsFragment parFrag = (SpectrometerSettingsFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_user_input);
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            mDeviceViewModel.setConnected(true);
                            makeToast("Now connected to " + mDeviceViewModel.getSelected().getName());
                            mProgressBar.setVisibility(View.INVISIBLE);
                            updateFragment();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            mProgressBar.setVisibility(View.VISIBLE);

                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            //Toast.makeText(activity, "Error, Disconnected",
                            // Toast.LENGTH_SHORT).show();
                            mDeviceViewModel.setConnected(false);
                            mProgressBar.setVisibility(View.INVISIBLE);
                            updateFragment();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    mProgressBar.setVisibility(View.VISIBLE);
                    //mText.setText(writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    //byte[] readBuf = (byte[]) msg.obj;
                    String readBuf = (String) msg.obj;
                    if (readBuf.equals("calibratedw") || readBuf.equals(" calibratedw")) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        makeToast("Graph Calibrated");
                    } else {
                        mDeviceViewModel.setSpectraAndWavelengths(new SpectraAndWavelengths(readBuf));
                        Log.d(TAG, "handleMessage: Clearing Entries");
                        mProgressBar.setVisibility(View.INVISIBLE);
                        makeToast("Data Received");

                    /*
                    // construct a string from the valid bytes in the buffer
                    //String readMessage = new String(readBuf, 0, msg.arg1);
                    String[] chartData = readBuf.split(" ");
                    for (int i = 0; i < chartData.length; ++i) {
                        Log.d(TAG, "handleMessage: Adding point (" + Integer.toString(mDeviceViewModel.getNumPoints()) +", " + Float.toString(Float.parseFloat(chartData[i])) + ")");
                        mDeviceViewModel.addData(new Entry(mDeviceViewModel.getNumPoints(), Float.parseFloat(chartData[i])));
                    }
                    */

                        mDeviceViewModel.refreshLineData("Message READ");
                        mDeviceViewModel.setDate(new Date());
                        mDeviceViewModel.setIsSaved(false);
                    }

                    //mText.setText("Data received and plotted.");
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDevice = msg.getData().getString(Constants.DEVICE_NAME);
                    if (this != null) {
                        //Toast.makeText(mainActivity, "Connected to " + connectedDeviceName,
                                //Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void sendInformation(float information, boolean isCalibration) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "No device connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (information > 0) {
            mBluetoothService.writeJson(information, isCalibration);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mTestEditText.setText(mOutStringBuffer);
        }
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


    @Override
    public void deviceClick() {
        if (!mConnectedDevice.equals(mDeviceViewModel.getSelected().getName())) {
            mBluetoothService.connect(mDeviceViewModel.getSelected(), false);
            mProgressBar.setVisibility(View.VISIBLE);
        }
        updateFragment();
    }

    private void updateFragment() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.detach(currentFragment);
            fragmentTransaction.attach(currentFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void parSendInformation(float information, boolean isCalibration) {
        mDeviceViewModel.clearEntries();
        sendInformation(information, isCalibration);
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
