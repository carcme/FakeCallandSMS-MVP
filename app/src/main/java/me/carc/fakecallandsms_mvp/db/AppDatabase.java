package me.carc.fakecallandsms_mvp.db;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import me.carc.fakecallandsms_mvp.app.App;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.model.FakeContact;
import me.carc.fakecallandsms_mvp.model.PreSetMsg;

/**
 * Applicaiton database (Room database)
 *
 * Created by bamptonm on 04/10/2017.
 */

@Database(entities = {FakeContact.class, PreSetMsg.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FakeContactDao fakeContactDao();
    public abstract PreSetMsgDao preSetMsgDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, App.DATABASE_NAME)
                            .fallbackToDestructiveMigrationFrom()
//                            .addMigrations(Migrations.MIGRATION_1_3)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final PreSetMsgDao preSetMsgDao;

        PopulateDbAsync(AppDatabase db) {
            preSetMsgDao = db.preSetMsgDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            // PRE-POPULATE THE DB's HERE
            if(C.DEBUG_ENABLED)
                preSetMsgDao.insertAll(PreSetMsg.populateData());

            return null;
        }
    }

}