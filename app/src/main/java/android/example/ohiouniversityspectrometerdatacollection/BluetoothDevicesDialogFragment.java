package android.example.ohiouniversityspectrometerdatacollection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class BluetoothDevicesDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bluetooth Devices")
                .setMessage("This is where the discoverable Bluetooth devices will appear");

        // Here we will call builder.setAdapter() to fill the dialog with a scrollable list
        // of discoverable bluetooth devices


        return builder.create();
    }
}
