package android.example.ohiouniversityspectrometerdatacollection;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;


public class SpectrometerSettingsFragment extends Fragment{
    // Debug
    private static final String TAG = "SpectrometerSettings";

    // Intent Request Codes
    private final static int REQUEST_ENABLE_BT = 1;

    // Member Fields

    private DeviceViewModel mDeviceViewModel;
    private ParametersInterface mCallback;

    // Layout Views
    private TextView mNotConnectedText;
    private EditText mIntegrationTimeEditText;
    private Button mCalibrationButton;
    private Button mSpectraButton;
    private RelativeLayout mUserInput;

    public interface ParametersInterface {
        void parSendInformation(float information, boolean isCalibration);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null ) {
            mDeviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
        }

        mDeviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (ParametersInterface) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_input, container, false);

        // Set Layout Views
        mNotConnectedText = rootView.findViewById(R.id.textview);
        mCalibrationButton = rootView.findViewById(R.id.calibration_button);
        mSpectraButton = rootView.findViewById(R.id.get_spectra_button);
        mIntegrationTimeEditText = rootView.findViewById(R.id.integration_time_edit);
        mUserInput = rootView.findViewById(R.id.parameters_input);


        if (mDeviceViewModel.getConnected()) {
            mUserInput.setVisibility(View.VISIBLE);
            mNotConnectedText.setVisibility(View.INVISIBLE);



            mCalibrationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Send a message using content of the edit text widget
                    float information = Float.parseFloat(mIntegrationTimeEditText.getText().toString());
                    mCallback.parSendInformation(information, true);
                }
            });

            mSpectraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float information = Float.parseFloat(mIntegrationTimeEditText.getText().toString());
                    mCallback.parSendInformation(information, false);
                }
            });

        } else {
            mUserInput.setVisibility(View.INVISIBLE);
            mNotConnectedText.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


}
