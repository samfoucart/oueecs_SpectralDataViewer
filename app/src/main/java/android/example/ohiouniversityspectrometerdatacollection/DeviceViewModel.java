package android.example.ohiouniversityspectrometerdatacollection;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.Toast;

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
    private SpectraAndWavelengths mSpectraAndWavelengths;

    // Device / Currently Plotted Graph
    private BluetoothDevice mSelectedDevice;
    private List<Entry> mEntries = new ArrayList<Entry>();
    private LineDataSet mDataSet;
    private LineData mLineData;
    private Boolean mConnected = false;
    private int mNumPoints = 0;
    private String mName;
    private Date mDate;
    private boolean mIsSaved = false;

    // Parameters
    private String mIntegrationTime;
    private int mAcquisitionMode = 0;
    private String mBoxcarWidth = "1";
    private String mScansToAverage = "1";


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

    public void setIsSaved(boolean isSaved) {
        mIsSaved = isSaved;
    }

    public boolean getIsSaved() {
        return mIsSaved;
    }

    public void setConnected(Boolean connected){
        mConnected = connected;
    }

    public void setSpectraAndWavelengths (SpectraAndWavelengths spectraAndWavelengths) {
        mSpectraAndWavelengths = spectraAndWavelengths;
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
        if (mSpectraAndWavelengths != null) {
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
            for (int i = 0; i < mSpectraAndWavelengths.getSpectraLength()
                         && i < mSpectraAndWavelengths.getWavelengthsLength(); ++i) {

                addData(new Entry(mSpectraAndWavelengths.getWavelengthAt(i),
                        mSpectraAndWavelengths.getSpectraAt(i)));
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

    public SpectraAndWavelengths getSpectraAndWavelengths() {
        return mSpectraAndWavelengths;
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
        mSpectraAndWavelengths = savedGraph.getLineData();
        mName = savedGraph.getName();
        mDate = savedGraph.getDate();
        mIsSaved = true;

        Toast.makeText(getApplication(), "Graph Loaded", Toast.LENGTH_SHORT).show();
        refreshLineData("loadGraphFromDate");
    }

    public String getIntegrationTime() {
        return mIntegrationTime;
    }

    public void setIntegrationTime(String mIntegrationTime) {
        this.mIntegrationTime = mIntegrationTime;
    }

    public int getAcquisitionMode() {
        return mAcquisitionMode;
    }

    public void setAcquisitionMode(int mAcquisitionMode) {
        this.mAcquisitionMode = mAcquisitionMode;
    }

    public String getBoxcarWidth() {
        return mBoxcarWidth;
    }

    public void setBoxcarWidth(String mBoxcarWidth) {
        this.mBoxcarWidth = mBoxcarWidth;
    }

    public String getScansToAverage() {
        return mScansToAverage;
    }

    public void setScansToAverage(String mScansToAverage) {
        this.mScansToAverage = mScansToAverage;
    }
}
