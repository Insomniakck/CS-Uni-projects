package com.example.androidgmail.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidgmail.entities.Label;
import java.util.List;

@Dao
public interface LabelDao {

    @Query("SELECT * FROM Label WHERE id = :id")
    LiveData<Label> get(String id);

    @Query("SELECT * FROM Label WHERE id = :id")
    Label getSync(String id);

    @Query("SELECT * FROM Label WHERE labelName = :name LIMIT 1")
    LiveData<Label> LabelName(String name);

    @Query("SELECT * FROM Label WHERE username = :username ")
    LiveData<List<Label>> getByUsername(String username);

    @Query("SELECT * FROM Label WHERE username = :username ")
    List<Label> getByUsernameSync(String username);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Label label);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Label> mails);

    @Update
    void update(Label... labels);

    @Query("DELETE FROM Label WHERE id = :id")
    void delete(String id);

}
