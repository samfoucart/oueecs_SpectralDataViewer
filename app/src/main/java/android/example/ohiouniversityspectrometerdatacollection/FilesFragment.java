package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class FilesFragment extends Fragment implements GraphClickCallback{
    private DeviceViewModel mDeviceViewModel;
    private RecyclerView mRecyclerView;
    private GraphListAdapter mGraphListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View itemView = inflater.inflate(R.layout.fragment_files, container, false);

        mRecyclerView = itemView.findViewById(R.id.saved_graph_recyclerview);
        mGraphListAdapter = new GraphListAdapter(getContext(), this);
        mRecyclerView.setAdapter(mGraphListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDeviceViewModel = ViewModelProviders.of(getActivity()).get(DeviceViewModel.class);

        mDeviceViewModel.getAllDates().observe(this, new Observer<List<Date>>() {
            @Override
            public void onChanged(@Nullable List<Date> dates) {
                mGraphListAdapter.setDates(dates);
                mGraphListAdapter.notifyDataSetChanged();
            }
        });

        mDeviceViewModel.getAllNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                mGraphListAdapter.setNames(strings);
                mGraphListAdapter.notifyDataSetChanged();
            }
        });


        return itemView;
    }

    @Override
    public void onGraphClick(Date date) {
        mDeviceViewModel.loadGraphFromDate(date);
    }
}
