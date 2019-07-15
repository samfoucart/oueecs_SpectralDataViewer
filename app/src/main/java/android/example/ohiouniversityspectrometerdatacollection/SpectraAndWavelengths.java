package android.example.ohiouniversityspectrometerdatacollection;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import static android.content.ContentValues.TAG;

public class SpectraAndWavelengths {
    private float[] mSpectra;
    private float[] mWavelengths;

    public SpectraAndWavelengths(String entry) {
        String splitData[] = entry.split("s ");
        String spectraStrings[] = splitData[0].split(" ");
        String wavelengthStrings[] = splitData[1].split(" ");
        mSpectra = new float[spectraStrings.length];
        mWavelengths = new float[wavelengthStrings.length - 1];
        try {
            for (int i = 0; i < spectraStrings.length && !spectraStrings[i].equals("s"); ++i) {
                if (!spectraStrings[i].equals("")) {
                    mSpectra[i] = Float.parseFloat(spectraStrings[i]);
                } else {
                    Log.d(TAG, "SpectraAndWavelengths: point missing");
                }
            }
            for (int i = 0; i < wavelengthStrings.length && !wavelengthStrings[i].equals("w"); ++i) {
                mWavelengths[i] = Float.parseFloat(wavelengthStrings[i]);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "SpectraAndWavelengths: ", e);
        }
    }

    public SpectraAndWavelengths(JSONArray spectra, JSONArray wavelengths){
        mSpectra = new float[spectra.length()];
        mWavelengths = new float[wavelengths.length()];
        try {
            for (int i = 0; i < spectra.length() && i < wavelengths.length(); i++) {
                mSpectra[i] = (float) spectra.getDouble(i);
                mWavelengths[i] = (float) wavelengths.getDouble(i);
            }
        } catch (JSONException e) {
            Log.d(TAG, "SpectraAndWavelengths: JSONException reading from array");
        }
    }

    public float[] getSpectra() {
        return mSpectra;
    }

    public float[] getWavelengths() {
        return mWavelengths;
    }

    public float getSpectraAt(int i) {
        return mSpectra[i];
    }

    public float getWavelengthAt(int i){
        return mWavelengths[i];
    }

    public int getSpectraLength(){
        return mSpectra.length;
    }

    public int getWavelengthsLength(){
        return mWavelengths.length;
    }

    public String dataToString() {
        StringBuilder totalStringBuilder = new StringBuilder();

        for (int i = 0; i < mSpectra.length; ++i) {
            totalStringBuilder.append(mSpectra[i]);
            totalStringBuilder.append(" ");
        }

        totalStringBuilder.append("s ");

        for (int i = 0; i < mWavelengths.length; ++i) {
            totalStringBuilder.append(mWavelengths[i]);
            totalStringBuilder.append(" ");
        }

        totalStringBuilder.append("w");

        return totalStringBuilder.toString();
    }
}
