package me.carc.fakecallandsms_mvp.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import me.carc.fakecallandsms_mvp.model.FakeContact;

/**
 * Applicaiton database (Room database)
 *
 * Created by bamptonm on 04/10/2017.
 */

@Database(entities = {FakeContact.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract FakeContactDao fakeContactDao();
}