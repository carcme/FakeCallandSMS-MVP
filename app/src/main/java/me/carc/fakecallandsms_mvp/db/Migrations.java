package me.carc.fakecallandsms_mvp.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

/**
 * Room DB migrations
 * Created by bamptonm on 27/10/2017.
 */

public class Migrations {

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE FakeContact ADD COLUMN mmsSubject TEXT");
            database.execSQL("ALTER TABLE FakeContact ADD COLUMN attachmentPath TEXT");
        }
    };
}
