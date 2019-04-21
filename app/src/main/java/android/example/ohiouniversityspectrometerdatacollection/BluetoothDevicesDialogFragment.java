package android.example.ohiouniversityspectrometerdatacollection;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;



import java.util.ArrayList;
import java.util.Set;


public class BluetoothDevicesDialogFragment extends DialogFragment implements DeviceRecyclerViewAdapter.OnDeviceListener {
    // Debug
    private static final String TAG = "BluetoothDevicesDialog";

    // Member Fields
    private BluetoothAdapter mBtAdapter;
    private RecyclerView mRecyclerView;
    private DeviceRecyclerViewAdapter mAdapter;
    private Context mContext;
    private DeviceViewModel mDeviceViewModel;
    private DeviceDialogListener mListener;

    // Newly discovered devices
    private ArrayList<BluetoothDevice> mDevices;

    public interface DeviceDialogListener {
        void deviceClick();
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mDevices = new ArrayList<>();

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View viewRoot = inflater.inflate(R.layout.dialog_bluetooth_devices, null);

        // Ask user to let the app access coarse location
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();

        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                mDevices.add(device);
            }
        }

        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        Log.d(TAG, "onCreate");
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);

        mBtAdapter.startDiscovery();

        mRecyclerView = viewRoot.findViewById(R.id.bluetooth_devices_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new DeviceRecyclerViewAdapter(getContext(), mDevices, this);
        mRecyclerView.setAdapter(mAdapter);

        mDeviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(viewRoot);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mListener = (DeviceDialogListener) context;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        Log.d(TAG, "OnDestroy");

        mContext.unregisterReceiver(mReceiver);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (device.getName() != null) {
                        mDevices.add(device);
                        mAdapter.notifyDataSetChanged();
                    }
                    Log.d(TAG, "Discovery - Name: " + device.getName() + "  --- Address: " + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "Discovery Finished");
            }
        }
    };

    @Override
    public void onDeviceClick(BluetoothDevice device) {
        mDeviceViewModel.select(device);
        mListener.deviceClick();
    }
}
