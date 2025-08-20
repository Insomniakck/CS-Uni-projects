package com.example.androidgmail.dao;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.entities.User;

@Database(
        entities = {
                User.class,
                Mail.class,
                Label.class
        },
        version = 3,
        exportSchema = false
)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract MailDao mailDao();
    public abstract UserDao userDao();
    public abstract LabelDao labelDao();

    private static volatile LocalDatabase INSTANCE;



    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE Mail ADD COLUMN senderFirstName TEXT");
            db.execSQL("ALTER TABLE Mail ADD COLUMN senderLastName  TEXT");
            db.execSQL("ALTER TABLE Mail ADD COLUMN senderProfileImage TEXT");
        }
    };


    public static LocalDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    LocalDatabase.class,
                                    "app_database.db")
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}
