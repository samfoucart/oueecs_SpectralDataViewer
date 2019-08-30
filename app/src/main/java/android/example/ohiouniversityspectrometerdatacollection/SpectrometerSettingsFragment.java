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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;


public class SpectrometerSettingsFragment extends Fragment {
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
    private EditText mBoxcarWidthEditText;
    private EditText mScansAverageEditText;
    private Button mCalibrationButton;
    private Button mSpectraButton;
    private RelativeLayout mUserInput;
    private Spinner mAcquisitionSpinner;
    private Button mCollectSampleButton;

    public interface ParametersInterface {
        void parSendInformation(float integrationTime, String testMode,
                                int boxcarWidth, int scansToAverage);
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
        mCollectSampleButton = rootView.findViewById(R.id.collect_sample_button);
        mIntegrationTimeEditText = rootView.findViewById(R.id.integration_time_edit);
        mBoxcarWidthEditText = rootView.findViewById(R.id.boxcar_width_edit);
        mScansAverageEditText = rootView.findViewById(R.id.scans_average_edit);
        mUserInput = rootView.findViewById(R.id.parameters_input);
        mAcquisitionSpinner = rootView.findViewById(R.id.acquisition_mode_spinner);
        ArrayAdapter<CharSequence> acquisitionAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.acquisition_modes, android.R.layout.simple_spinner_item);
        acquisitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAcquisitionSpinner.setAdapter(acquisitionAdapter);
        mAcquisitionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mDeviceViewModel.setAcquisitionMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mAcquisitionSpinner.setSelection(mDeviceViewModel.getAcquisitionMode());

        mIntegrationTimeEditText.setText(mDeviceViewModel.getIntegrationTime());
        mBoxcarWidthEditText.setText(mDeviceViewModel.getBoxcarWidth());
        mScansAverageEditText.setText(mDeviceViewModel.getScansToAverage());

        if (mDeviceViewModel.getConnected()) {
            mUserInput.setVisibility(View.VISIBLE);
            mNotConnectedText.setVisibility(View.INVISIBLE);



            mCalibrationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Send a message using content of the edit text widget
                    if (areParametersValid()) {
                        float integrationTime = Float.parseFloat(mIntegrationTimeEditText.getText().toString());
                        int boxcarWidth = Integer.parseInt(mBoxcarWidthEditText.getText().toString());
                        int scansToAverage = Integer.parseInt(mScansAverageEditText.getText().toString());
                        mCallback.parSendInformation(integrationTime, "Background",
                                boxcarWidth, scansToAverage);
                    }
                }
            });

            mSpectraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (areParametersValid()) {
                        float integrationTime = Float.parseFloat(mIntegrationTimeEditText.getText().toString());
                        int boxcarWidth = Integer.parseInt(mBoxcarWidthEditText.getText().toString());
                        int scansToAverage = Integer.parseInt(mScansAverageEditText.getText().toString());
                        mCallback.parSendInformation(integrationTime, "Reference",
                                boxcarWidth, scansToAverage);
                    }
                }
            });

            mCollectSampleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (areParametersValid()) {
                        float integrationTime = Float.parseFloat(mIntegrationTimeEditText.getText().toString());
                        int boxcarWidth = Integer.parseInt(mBoxcarWidthEditText.getText().toString());
                        int scansToAverage = Integer.parseInt(mScansAverageEditText.getText().toString());
                        mCallback.parSendInformation(integrationTime,
                                mAcquisitionSpinner.getSelectedItem().toString(),
                                boxcarWidth, scansToAverage);
                    }
                }
            });

        } else {
            mUserInput.setVisibility(View.INVISIBLE);
            mNotConnectedText.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeviceViewModel.setIntegrationTime(mIntegrationTimeEditText.getText().toString());
        mDeviceViewModel.setBoxcarWidth(mBoxcarWidthEditText.getText().toString());
        mDeviceViewModel.setScansToAverage(mScansAverageEditText.getText().toString());
    }

    private boolean areParametersValid() {

        if (mIntegrationTimeEditText.getText().toString().equals("")) {
            Toast.makeText(getContext(),
                    "Integration Time Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Integer.parseInt(mBoxcarWidthEditText.getText().toString()) < 1 ||
                Integer.parseInt(mBoxcarWidthEditText.getText().toString()) > 10) {
            Toast.makeText(getContext(),
                    "Boxcar width must be between 1 and 10", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Integer.parseInt(mScansAverageEditText.getText().toString()) < 1 ||
                Integer.parseInt(mScansAverageEditText.getText().toString()) > 10) {
            Toast.makeText(getContext(),
                    "Scans to average must be between 1 and 10", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
