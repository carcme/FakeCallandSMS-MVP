package me.carc.fakecallandsms_mvp.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import me.carc.fakecallandsms_mvp.model.PreSetMsg;

/**
 * Favorites Data Access Object (DAO)
 * Created by bamptonm on 03/10/2017.
 */

@Dao
public interface PreSetMsgDao {

    @Query("SELECT * FROM PreSetMsg")
    LiveData<List<PreSetMsg>> getAllEntries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PreSetMsg entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(PreSetMsg... dataEntities);

    @Update
    void update(PreSetMsg entry);

    @Delete
    void delete(PreSetMsg entry);

    @Query("DELETE FROM PreSetMsg")
    void nukeTable();
}
