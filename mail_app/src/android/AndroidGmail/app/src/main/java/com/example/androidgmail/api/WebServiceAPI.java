package com.example.androidgmail.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.entities.User;
import com.example.androidgmail.helpers.LabelMailBody;
import java.util.List;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface WebServiceAPI {

//~~~~~~~~~~Mails~~~~~~~~~~~~~~~~~~~
    @GET("mails")
    Call<List<Mail>> getAllUserMails();

    @POST("mails")
    Call<String>  createMail(@Body Mail mail);

    @GET("mails/search/{query}")
    Call<List<Mail>> searchString(@Path("query") String query);

    @POST("mails/draft")
    Call<Mail> createDraft(@Body Mail mail);

    @PATCH("mails/draft/{id}")
    Call<Void> editDraft(@Path("id") String id, @Body Mail mail);

    @GET("mails/{id}")
    Call<Mail> getMail(@Path("id") String id);

    @PATCH("mails/{id}")
    Call<Void> editMail(@Path("id") String id, @Body Mail mail);

    @DELETE("mails/{id}")
    Call<Void> deleteMail(@Path("id") String id);


//~~~~~~~~~~~~User~~~~~~~~~~~~
    @Multipart
    @POST("users")
    Call<String> createUser(
            @Part MultipartBody.Part profileImage,
            @Part("firstName") RequestBody firstName,
            @Part("lastName") RequestBody lastName,
            @Part("birthday") RequestBody birthday,
            @Part("username") RequestBody username,
            @Part("password") RequestBody password
    );


    @GET("users/username/{username}")
    Call<User> getUserInfo(@Path("username") String username);

    @GET("users/{id}")
    Call<User> getUserByID(@Path("id") String id);

    @POST("users/folder")
    Call<Map<String,String>> getUserDefaultLabels();

    @POST("users/folderID")
    Call<String> getUserDefaultLabelsByName(@Body Map<String, String> body);


//~~~~~~~~~~~~Token~~~~~~~~~~~~
    @POST("tokens")
    Call<String> loginUser(@Body Map<String, String> credentials);


    @POST("tokens")
    Call<String> login(@Body User user);

//~~~~~~~~~~Labels~~~~~~~~~~~~~
    @GET("labels")
    Call<List<Label>> getAllLabels();

    @POST("labels")
    Call<String> createLabel(@Body Label label);

    @GET("labels/{labelID}")
    Call<Label> getLabel(@Path("labelID") String labelID);

    @PATCH("labels/{labelID}")
    Call<Void> editLabel(@Path("labelID") String labelID, @Body Label label);

    @DELETE("labels/{labelID}")
    Call<Void> deleteLabel(@Path("labelID") String labelID);

    @POST("labels/mail")
    Call<Void> addMailToLabel(@Body LabelMailBody ids);

    @POST("labels/mail/remove")
    Call<Void> removeMailFromLabel(@Body LabelMailBody ids);

    @GET("labels/mail/{labelID}")
    Call<Map<String, Object>> getMailsForLabel(@Path("labelID") String labelID);



}