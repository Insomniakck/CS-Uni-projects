package com.example.androidgmail.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.androidgmail.BuildConfig;
import com.example.androidgmail.api.UserConverter;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

@Entity
public class User {
    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;

    private String firstName;
    private String lastName;
    private String birthday;
    @SerializedName("username")    private String username;
    private String password;

    private String profileImage;

    @TypeConverters(UserConverter.class)
    @SerializedName("defaultLabels")
    private Map<String,String> defaultLabels;



    /* --- constructors --- */

    public User(@NonNull String id,
                String firstName,
                String lastName,
                String birthday,
                String username,
                String password) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.birthday  = birthday;
        this.username  = username;
        this.password  = password;

    }

    public User(String first,
                String last,
                String bday,
                String email,
                String pwd) {
        this.id = "";
        this.firstName = first;
        this.lastName = last;
        this.birthday = bday;
        this.username = email;
        this.password = pwd;
    }

    public User(String first,
                String last,
                String username,
                String profileImage) {
        this.id = "";
        this.firstName = first;
        this.lastName = last;
        this.username = username;
        this.profileImage = profileImage;
    }




    /* --- getters --- */

    @NonNull
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getBirthday() { return birthday; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }
    public String getProfileImage() { return profileImage; }

    public String getProfileImageUrl() {
        if (profileImage == null || profileImage.isEmpty()) return null;
        if (!profileImage.startsWith("http")) {
            profileImage = "http://" + BuildConfig.SERVER_IP + ":8080/uploads/" + profileImage;
        }
        return profileImage;
    }

    public String getLabelID(String labelName) {
        return defaultLabels == null ? null : defaultLabels.get(labelName);
    }

    public Map<String, String> getDefaultLabels() {
        return defaultLabels;
    }


    /* --- setters --- */

    public void setDefaultLabels(Map<String,String> labelIDs){
        this.defaultLabels = labelIDs;
    }


    public void setId(@NonNull String id) { this.id = id; }
    public void setFirstName( String firstName) { this.firstName = firstName; }
    public void setLastName( String lastName) { this.lastName = lastName; }
    public void setBirthday( String birthday) { this.birthday = birthday; }

    public void setUsername( String username) { this.username = username; }

    public void setPassword( String password) { this.password = password; }
    public void setProfileImage(String url) { this.profileImage = url; }

}
