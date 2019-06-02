package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mDeviceViewModel.getLineData() != null){
            mNoGraphText.setVisibility(View.INVISIBLE);
            mChart.setData(mDeviceViewModel.getLineData());
            mChart.invalidate();
            mChart.setVisibility(View.VISIBLE);
            mChart.setPinchZoom(true);
            mChart.setMaxVisibleValueCount(1);
        } else {
            mNoGraphText.setVisibility(View.VISIBLE);
            mChart.setVisibility(View.INVISIBLE);
        }
    }
}
