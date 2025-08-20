package com.example.androidgmail.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidgmail.entities.User;
import java.util.List;
import androidx.lifecycle.LiveData;


@Dao
public interface UserDao {

    @Query("SELECT * FROM User")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM User WHERE id = :id")
    LiveData<User> get(String id);


    @Query("SELECT * FROM User WHERE id = :id")
    User getSync(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User... users);

    @Update
    void update(User... users);

    @Query("DELETE FROM User WHERE id = :id")
    void delete(String id);

}
