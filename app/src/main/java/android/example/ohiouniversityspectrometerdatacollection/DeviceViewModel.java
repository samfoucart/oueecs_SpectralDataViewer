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
    private Boolean mConnected = false;
    private int mNumPoints = 0;

    public void select(BluetoothDevice device){
        mSelectedDevice = device;
    }

    public void clearEntries() {
        mNumPoints = 0;
        mEntries = new ArrayList<Entry>();
    }

    public void setConnected(Boolean connected){
        mConnected = connected;
    }

    public void addData(Entry entry) {
        mNumPoints++;
        mEntries.add(entry);

        // Add entries to the dataset
        //mDataSet = new LineDataSet(mEntries, "Label");

        //mLineData = new LineData(mDataSet);
    }

    public void refreshLineData(String label) {
        mDataSet = new LineDataSet(mEntries, label);

        mLineData = new LineData(mDataSet);
    }

    public void resetNumPoints() {
        mNumPoints = 0;
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

    public Boolean getConnected() {
        return mConnected;
    }

    public int getNumPoints() { return mNumPoints; }

}
