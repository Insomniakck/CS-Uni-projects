package com.example.androidgmail.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.androidgmail.entities.Mail;
import java.util.List;

@Dao
public interface MailDao {

    @Query("SELECT * FROM Mail ORDER BY date DESC")
    LiveData<List<Mail>> getAll();

    @Query("SELECT * FROM Mail WHERE id IN (:mailIds)")
    LiveData<List<Mail>> loadByIds(List<String> mailIds);


    @Query("SELECT * FROM Mail WHERE id IN (:idList)")
    LiveData<List<Mail>> getList(List<String> idList);


    @Query("SELECT * FROM Mail")
    List<Mail> index();

    @Query("SELECT * FROM Mail WHERE id = :id")
    LiveData<Mail> get(String id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Mail... mails);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Mail> mails);


    @Update
    void update(Mail... mails);

    @Query("DELETE FROM Mail WHERE id = :id")
    void delete(String id);

    @Query(
            "SELECT * FROM Mail " +
                    "WHERE subject LIKE '%' || :query || '%' " +
                    "OR sender LIKE '%' || :query || '%' " +
                    "OR receiver LIKE '%' || :query || '%' " +
                    "OR content LIKE '%' || :query || '%'"
    )
    LiveData<List<Mail>> search(String query);


//    Adds new mails to room, if they already exist, update them
    default void upsert(List<Mail> mails) {
        insertAll(mails);
        update(mails.toArray(new Mail[0]));
    }

    @Query("SELECT * FROM Mail")
    List<Mail> getAllSync();

    @Query("DELETE FROM Mail")
    void clear();



}


