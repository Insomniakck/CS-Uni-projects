package com.example.androidgmail.entities;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.androidgmail.BuildConfig;
import com.google.gson.annotations.SerializedName;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Entity
public class Mail {

    @PrimaryKey
    @NonNull
    @SerializedName("_id")
    private String id;
    private String subject;
    private String receiver;
    private String sender;
    private String content;
    private String date;
    @ColumnInfo(name = "senderFirstName") private String senderFirstName;
    @ColumnInfo(name = "senderLastName") private String senderLastName;
    @ColumnInfo(name = "senderProfileImage") private String senderProfileImage;

    private boolean isDraft = false;

    private boolean isSpam = false;

    private boolean isTrash = false;

    private boolean isStar = false;

    private boolean isRead = false;

    /* --- constructors --- */

    public Mail( @NonNull String id, String sender, String receiver, String subject, String content) {
        this.id = id;
        this.subject = subject;
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.US).format(new Date());
    }

    @Ignore
    public Mail(String sender, String receiver, String subject, String content) {
        this.id = "";
        this.subject = subject;
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.US).format(new Date());
    }




    /* --- setters --- */


    public void setId(@NonNull String id) {
        this.id = id;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    public void setSenderFirstName(String firstName)   { this.senderFirstName = firstName; }
    public void setSenderLastName(String lastName)    { this.senderLastName  = lastName; }
    public void setSenderProfileImage(String profile) { this.senderProfileImage = profile; }
    public void setDraft( boolean isDraft ) { this.isDraft = isDraft; }
    public void setSpam( boolean isSpam ) { this.isSpam = isSpam; }
    public void setTrash( boolean isTrash ) { this.isTrash = isTrash; }
    public void setStar( boolean isStar ) { this.isStar = isStar; }
    public void setRead(boolean read) { this.isRead = read; }


    /* --- getters --- */

    public boolean getStar() { return isStar; }

    public boolean getDraft() { return isDraft;}

    public boolean getSpam() { return isSpam;}

    public boolean getTrash() { return isTrash;}

    public boolean isRead() { return isRead; }

    @NonNull
    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }
    public String getSenderFirstName()  { return senderFirstName; }
    public String getSenderLastName()   { return senderLastName; }
    public String getSenderProfileImage(){
        if (senderProfileImage == null || senderProfileImage.isEmpty()) return null;
        if (!senderProfileImage.startsWith("http")) {
            senderProfileImage = "http://" + BuildConfig.SERVER_IP + ":8080/uploads/" + senderProfileImage;
        }
        return senderProfileImage;
    }

    public String getParsedDate() {
        Log.d("Mail", "Raw date before parse: " + date);


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);
                LocalDateTime mailDateTime = offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();

                LocalDate mailDate = mailDateTime.toLocalDate();
                LocalDate today = LocalDate.now();
                Locale currentLocale = Locale.getDefault();

                if (mailDate.isEqual(today)) {
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", currentLocale);
                    String result = mailDateTime.format(timeFormatter);
                    return result;
                } else {
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d", currentLocale);
                    String result = mailDate.format(dateFormatter);
                    return result;
                }
            }
        } catch (Exception e) {
            Log.e("Mail", "Failed to parse date: " + date, e);
            return date;
        }

        return date; // fallback
    }


    /* Return Formatted Date Function*/
    public String getDate() {
        return date;
    }


}
