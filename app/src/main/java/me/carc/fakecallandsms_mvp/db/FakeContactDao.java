package me.carc.fakecallandsms_mvp.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import me.carc.fakecallandsms_mvp.model.FakeContact;

/**
 * Favorites Data Access Object (DAO)
 * Created by bamptonm on 03/10/2017.
 */

@Dao
public interface FakeContactDao {

    @Query("SELECT * FROM FakeContact")
    List<FakeContact> getAllEntries();

    @Query("SELECT * FROM FakeContact WHERE keyID LIKE :id LIMIT 1")
    FakeContact findByIndex(long id);

    @Insert
    void insertAll(List<FakeContact> entries);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FakeContact entry);

    @Update
    void update(FakeContact entry);

    @Delete
    void delete(FakeContact entry);

    @Query("DELETE FROM FakeContact")
    void nukeTable();
}
