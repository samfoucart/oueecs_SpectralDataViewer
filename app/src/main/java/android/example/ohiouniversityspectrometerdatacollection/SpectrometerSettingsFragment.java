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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;


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
    private ParametersInterface mCallback;

    // Layout Views
    private TextView mText;
    private EditText mTestEditText;
    private Button mTestButton;

    public interface ParametersInterface {
        void parSendInformation(String information);
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
        mText = rootView.findViewById(R.id.textview);
        mTestButton = rootView.findViewById(R.id.test_button);
        mTestEditText = rootView.findViewById(R.id.test_edit_text);

        if (mDeviceViewModel.getSelected() != null){
            mText.setText(mDeviceViewModel.getSelected().getName() + " is selected.");
        } else {
            Log.d(TAG, "onCreateView: Device NULL");
        }


        if (mDeviceViewModel.getConnected()) {
            mTestButton.setVisibility(View.VISIBLE);
            mTestEditText.setVisibility(View.VISIBLE);


            mTestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Send a message using content of the edit text widget
                    String information = mTestEditText.getText().toString();
                    mCallback.parSendInformation(information);
                }
            });

        } else {
            mTestButton.setVisibility(View.INVISIBLE);
            mTestEditText.setVisibility(View.INVISIBLE);
        }




        return rootView;
    }

    public void connectedVisible() {
        mTestButton.setVisibility(View.VISIBLE);
        mTestEditText.setVisibility(View.VISIBLE);

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                String information = mTestEditText.getText().toString();
                mCallback.parSendInformation(information);
            }
        });
    }

    public void disconnectedInvisible() {
        mTestButton.setVisibility(View.INVISIBLE);
        mTestEditText.setVisibility(View.INVISIBLE);
    }

    public void echoPlotted() {
        mText.setText("Data Received and Plotted");
    }

}
