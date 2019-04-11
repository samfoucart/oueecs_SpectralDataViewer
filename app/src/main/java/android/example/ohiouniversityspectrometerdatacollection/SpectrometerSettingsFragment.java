package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class SpectrometerSettingsFragment extends Fragment{
    private static final String TAG = "SpectrometerSettings";

    // Member Fields
    private BluetoothAdapter mBtAdapter;
    private TextView mText;
    private BluetoothDevice mDevice = null;
    private DeviceViewModel mDeviceViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null ) {
            mDeviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_input, container, false);
        mText = rootView.findViewById(R.id.textview);
        if (mDeviceViewModel.getSelected() != null){
            mText.setText(mDeviceViewModel.getSelected().getName() + " is selected.");
        } else {
            Log.d(TAG, "onCreateView: Device NULL");
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }
}
