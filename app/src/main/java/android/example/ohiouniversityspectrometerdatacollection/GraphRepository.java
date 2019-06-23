package android.example.ohiouniversityspectrometerdatacollection;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;


import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;


public class GraphRepository {
    private SavedGraph queryResult;
    private GraphLoadedCallback mGraphLoadedCallback;

    private GraphDao mGraphDao;
    //private LiveData<List<Integer>> mAllIds;
    private LiveData<List<String>> mAllNames;
    private LiveData<List<Date>> mAllDates;

    public GraphRepository(Application application, GraphLoadedCallback graphLoadedCallback) {
        GraphRoomDatabase db = GraphRoomDatabase.getDatabase(application);
        mGraphDao = db.graphDao();
        //mAllIds = mGraphDao.getAllIds();
        mAllNames = mGraphDao.getAllNames();
        mAllDates = mGraphDao.getAllDates();
        mGraphLoadedCallback = graphLoadedCallback;
    }

    /*
    LiveData<List<Integer>> getAllIds() {
        return mAllIds;
    }
    */

    LiveData<List<String>> getAllNames() {
        return mAllNames;
    }

    LiveData<List<Date>> getAllDates() {
        return mAllDates;
    }

    /*
    String getNameFromId(Integer entry) {
        return mGraphDao.getNameFromId(entry);
    }

    Date getDateFromId(Integer entry) {
        return mGraphDao.getDateFromId(entry);
    }
    */

    public void getGraphFromDate(Date date) {
        Log.d(TAG, "getGraphFromDate: " + date.toString());
        getGraphFromDateAsyncTask task = new getGraphFromDateAsyncTask(mGraphDao);
        task.delegate = this;
        task.execute(date);
    }

    public SavedGraph getQueryResult() {
        return queryResult;
    }



    public void insert (SavedGraph savedGraph) {
        new insertAsyncTask(mGraphDao).execute(savedGraph);
    }

    private static class insertAsyncTask extends AsyncTask<SavedGraph, Void, Void> {
        private GraphDao mAsyncTaskDao;

        insertAsyncTask(GraphDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SavedGraph... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private void asyncFinished(SavedGraph result) {
        queryResult = result;
        mGraphLoadedCallback.onGraphLoaded(queryResult);
    }


    private static class getGraphFromDateAsyncTask extends AsyncTask<Date, Void, SavedGraph> {
        private GraphDao mAsyncTaskDao;
        private GraphRepository delegate = null;

        getGraphFromDateAsyncTask(GraphDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected SavedGraph doInBackground(final Date... params) {
            return mAsyncTaskDao.getGraphFromDate(params[0]);
        }

        @Override
        protected void onPostExecute(SavedGraph result) {
            delegate.asyncFinished(result);
        }

    }

    public interface GraphLoadedCallback {
        void onGraphLoaded(SavedGraph savedGraph);

    }
}
