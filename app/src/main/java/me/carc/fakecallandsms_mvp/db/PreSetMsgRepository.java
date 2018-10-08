package me.carc.fakecallandsms_mvp.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import me.carc.fakecallandsms_mvp.model.PreSetMsg;


/**
 * Created by bamptonm on 12/09/2018.
 */

public class PreSetMsgRepository {
    private PreSetMsgDao mDao;

    PreSetMsgRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDao = db.preSetMsgDao();
    }

    LiveData<List<PreSetMsg>> getAllMessages() {
        return mDao.getAllEntries();
    }

    void insert (PreSetMsg tour) {
        new insertAsyncTask(mDao).execute(tour);
    }

    void delete(PreSetMsg tour) {
        new deleteAsyncTask(mDao).execute(tour);
    }

    void nuke() {
        new nukeAsyncTask(mDao).execute();
    }

    private static class insertAsyncTask extends AsyncTask<PreSetMsg, Void, Void> {
        private PreSetMsgDao mAsyncTaskDao;

        insertAsyncTask(PreSetMsgDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PreSetMsg... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<PreSetMsg, Void, Void> {
        private PreSetMsgDao mAsyncTaskDao;

        deleteAsyncTask(PreSetMsgDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PreSetMsg... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class nukeAsyncTask extends AsyncTask<PreSetMsg, Void, Void> {
        private PreSetMsgDao mAsyncTaskDao;

        nukeAsyncTask(PreSetMsgDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final PreSetMsg... params) {
            mAsyncTaskDao.nukeTable();
            return null;
        }
    }
}