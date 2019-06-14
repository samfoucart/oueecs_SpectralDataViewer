package android.example.ohiouniversityspectrometerdatacollection;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;




public class DeviceViewModel extends AndroidViewModel implements GraphRepository.GraphLoadedCallback {
    String TAG = "DeviceViewModel";
    // Saved Graphs

    private GraphRepository mRepository;
    //private LiveData<List<Integer>> mAllIds;
    private LiveData<List<String>> mAllNames;
    private LiveData<List<Date>> mAllDates;
    private String mGraphString;

    // Device / Currently Plotted Graph
    private BluetoothDevice mSelectedDevice;
    private List<Entry> mEntries = new ArrayList<Entry>();
    private LineDataSet mDataSet;
    private LineData mLineData;
    private Boolean mConnected = false;
    private int mNumPoints = 0;
    private String mName;
    private Date mDate;


    public DeviceViewModel (Application application) {
        super(application);
        mRepository = new GraphRepository(application, this);
        //mAllIds = mRepository.getAllIds();
        mAllNames = mRepository.getAllNames();
        mAllDates = mRepository.getAllDates();

    }


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

    public void setGraphString (String graphString) {
        mGraphString = graphString;
    }

    public void addData(Entry entry) {
        mNumPoints++;
        mEntries.add(entry);

        // Add entries to the dataset
        //mDataSet = new LineDataSet(mEntries, "Label");

        //mLineData = new LineData(mDataSet);
    }

    public Date getDate() {
        return mDate;
    }

    public String getName() {
        return mName;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setName(String name) {
        mName = name;
    }

    public void refreshLineData(String label) {
        if (mGraphString != null) {
            // construct a string from the valid bytes in the buffer
            //String readMessage = new String(readBuf, 0, msg.arg1);

            // Clear all data in the graph
            resetNumPoints();
            if (mEntries != null) {
                mEntries.clear();
            }
            if (mDataSet != null) {
                mDataSet.clear();
            }
            if (mLineData != null) {
                mLineData.clearValues();
            }

            // Create an array of strings by splitting the string of all points, then parse
            // each string in the array as a float and add it to the list
            String[] chartData = mGraphString.split(" ");
            for (int i = 0; i < chartData.length; ++i) {
                Log.d(TAG, "refreshLineData: Adding point (" + Integer.toString(getNumPoints()) + ", " + Float.toString(Float.parseFloat(chartData[i])) + ")");
                addData(new Entry(getNumPoints(), Float.parseFloat(chartData[i])));
            }
            mDataSet = new LineDataSet(mEntries, label);
            mLineData = new LineData(mDataSet);
            
        } else {
            Log.d(TAG, "refreshLineData: no GraphString");
        }
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

    public String getGraphString() {
        return mGraphString;
    }

    public int getNumPoints() { return mNumPoints; }

    /*
    public LiveData<List<Integer>> getAllIds() { return mAllIds; }
    */

    public LiveData<List<String>> getAllNames() { return mAllNames; }

    public LiveData<List<Date>> getAllDates() { return mAllDates; }

    public void insert(SavedGraph savedGraph) { mRepository.insert(savedGraph); }

    public void loadGraphFromDate(Date date) {
        mRepository.getGraphFromDate(date);

    }

    @Override
    public void onGraphLoaded(SavedGraph savedGraph) {
        SavedGraph tmp = savedGraph;
        mGraphString = tmp.getLineData();
        mName = tmp.getName();
        mDate = tmp.getDate();

        refreshLineData("loadGraphFromDate");
    }
}
