package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import org.w3c.dom.Text;

public class GraphFragment extends Fragment {
    // Debug
    private static final String TAG = "GraphFragment";

    // Member Fields
    private DeviceViewModel mDeviceViewModel;
    private LineChart mChart;
    private TextView mNoGraphText;
    private RelativeLayout mDisplay;
    private TextView mDateTextView;
    private EditText mNameEditText;
    private Button mSaveButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        mChart = rootView.findViewById(R.id.selected_chart);
        mNoGraphText = rootView.findViewById(R.id.no_graph_text);
        mDisplay = rootView.findViewById(R.id.graph_display);
        mDateTextView = rootView.findViewById(R.id.graph_date);
        mNameEditText = rootView.findViewById(R.id.graph_title);
        mSaveButton = rootView.findViewById(R.id.save_graph_button);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceViewModel.insert(new SavedGraph(mDeviceViewModel.getSpectraAndWavelengths(),
                        mNameEditText.getText().toString(),
                        mDeviceViewModel.getDate()));

            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mDeviceViewModel.getLineData() != null){
            mNoGraphText.setVisibility(View.INVISIBLE);
            mChart.setData(mDeviceViewModel.getLineData());
            mChart.invalidate();
            mDisplay.setVisibility(View.VISIBLE);
            mChart.setPinchZoom(true);
            mChart.setMaxVisibleValueCount(1);
            mDateTextView.setText(mDeviceViewModel.getDate().toString());
        } else {
            mNoGraphText.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.INVISIBLE);
        }
    }
}
