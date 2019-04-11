package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.lifecycle.ViewModel;
import android.bluetooth.BluetoothDevice;

public class DeviceViewModel extends ViewModel {
    private BluetoothDevice mSelectedDevice;

    public void select(BluetoothDevice device){
        mSelectedDevice = device;
    }

    public BluetoothDevice getSelected() {
        return mSelectedDevice;
    }
}
