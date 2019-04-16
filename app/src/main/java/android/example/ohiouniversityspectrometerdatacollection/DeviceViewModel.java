package android.example.ohiouniversityspectrometerdatacollection;

import android.arch.lifecycle.ViewModel;
import android.bluetooth.BluetoothDevice;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class DeviceViewModel extends ViewModel {
    private BluetoothDevice mSelectedDevice;
    private List<Entry> mEntries = new ArrayList<Entry>();
    private LineDataSet mDataSet;
    private LineData mLineData;

    public void select(BluetoothDevice device){
        mSelectedDevice = device;
    }

    public void clearEntries() {
        mEntries = new ArrayList<Entry>();
    }

    public void addData(Entry entry) {
        mEntries.add(entry);

        // Add entries to the dataset
        mDataSet = new LineDataSet(mEntries, "Label");

        mLineData = new LineData(mDataSet);
    }

    public BluetoothDevice getSelected() {
        return mSelectedDevice;
    }

    public List<Entry> getList() {
        return mEntries;
    }

    public LineData getLineData() {
        return mLineData;
    }
}
