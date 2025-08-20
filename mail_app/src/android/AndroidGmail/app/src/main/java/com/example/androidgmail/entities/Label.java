package com.example.androidgmail.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.androidgmail.api.LabelConverter;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/** Converts List<String> to and from JSON for the “mails” column */
@Entity
@TypeConverters(LabelConverter.class)
public class Label {

    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;

    private String labelName;
    private String username;

    /** List of Mail _id strings */
    private List<String> mails;

    public Label() {

    }


    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMails(List<String> mails) {
        this.mails = mails;
    }

    /* --- constructors --- */
    public Label(@NonNull String id, String labelName, String username, List<String> mails) {
        this.id        = id;
        this.labelName = labelName;
        this.username  = username;
        this.mails     = mails;
    }

    @Ignore
    public Label( String labelName, String username, List<String> mails) {
        this.id        = "";
        this.labelName = labelName;
        this.username  = username;
        this.mails     = mails;
    }


    /* --- getters --- */
    @NonNull
    public String getId() {
        return id;
    }
    public String getLabelName() { return labelName; }
    public String getUsername() { return username; }
    public List<String> getMails() { return mails; }

    public void setID(String id) {
        this.id = id;
    }

}
