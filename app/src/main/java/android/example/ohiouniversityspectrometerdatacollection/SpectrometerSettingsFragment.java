package android.example.ohiouniversityspectrometerdatacollection;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class SpectrometerSettingsFragment extends Fragment{
    // Debug
    private static final String TAG = "SpectrometerSettings";

    // Intent Request Codes
    private final static int REQUEST_ENABLE_BT = 1;

    // Member Fields
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mBluetoothService;
    private DeviceViewModel mDeviceViewModel;
    private StringBuffer mOutStringBuffer;

    // Layout Views
    private TextView mText;
    private EditText mTestEditText;
    private Button mTestButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null ) {
            mDeviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
        }

        // Get the local bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getContext(), "Bluetooth is not available on this device.", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        mDeviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_input, container, false);

        // Set Layout Views
        mText = rootView.findViewById(R.id.textview);
        mTestButton = rootView.findViewById(R.id.test_button);
        mTestEditText = rootView.findViewById(R.id.test_edit_text);

        if (mDeviceViewModel.getSelected() != null){
            mText.setText(mDeviceViewModel.getSelected().getName() + " is selected.");
        } else {
            Log.d(TAG, "onCreateView: Device NULL");
        }

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
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
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (mBluetoothService != null) {
            if (mBluetoothService.getState() == BluetoothService.STATE_NONE) {
                mBluetoothService.start();
            }
        }
    }

    private void setupCommunication() {
        Log.d(TAG, "SetupCommunication()");

        // Initialize the BluetoothService to perform bluetooth connections
        mBluetoothService = new BluetoothService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");

        // Connect to device if it's selected
        if (mDeviceViewModel.getSelected() != null) {
            mBluetoothService.connect(mDeviceViewModel.getSelected(), false);
            mTestButton.setVisibility(View.VISIBLE);
            mTestEditText.setVisibility(View.VISIBLE);
            mTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Send a message using content of the edit text widget
                    String information = mTestEditText.getText().toString();
                    sendInformation(information);
                }
            });
        }

    }

    private void sendInformation(String information) {
        // Check that we're actually connected before trying anything
        if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), "No device connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (information.length() > 0) {
            // Get the message bytes and tel the BluetoothService to write
            byte[] send = information.getBytes();
            mBluetoothService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mTestEditText.setText(mOutStringBuffer);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            //Toast.makeText(activity, "Error, Disconnected",
                                   // Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mText.setText(writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mText.setText(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (activity != null) {
                        Toast.makeText(activity, "Connected to " + connectedDeviceName,
                                Toast.LENGTH_SHORT).show();
                    }
                    break;


            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a communication
                    setupCommunication();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getActivity(), "Bluetooth not enabled",
                            Toast.LENGTH_SHORT).show();
                }
        }
    }


}
