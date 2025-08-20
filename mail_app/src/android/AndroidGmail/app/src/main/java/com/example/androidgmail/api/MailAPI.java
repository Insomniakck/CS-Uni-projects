package com.example.androidgmail.api;

import android.content.Context;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidgmail.BuildConfig;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.repositories.SessionManager;
import java.util.List;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MailAPI {
    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public MailAPI(Context ctx) {

        // Attach header to every request
        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("X-User-Id", SessionManager.getUserId(ctx))
                                .build()))
                .build();

        // Call the backend server
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + BuildConfig.SERVER_IP + ":8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(ok)
                .build();
        webServiceAPI = retrofit.create(WebServiceAPI.class);
    }

    /* ──────────── tiny helpers to avoid repetition ──────────── */
    private <T> LiveData<T> toLiveData(Call<T> call) {
        MutableLiveData<T> live = new MutableLiveData<>();
        call.enqueue(new Callback<T>() {
            @Override public void onResponse(Call<T> c, Response<T> r) {
                live.postValue(r.isSuccessful() ? r.body() : null);
            }
            @Override public void onFailure(Call<T> c, Throwable t) {
                live.postValue(null);
            }
        });
        return live;
    }

    private LiveData<Boolean> toBoolLiveData(Call<Void> call) {
        MutableLiveData<Boolean> live = new MutableLiveData<>();
        call.enqueue(new Callback<Void>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) {
                live.postValue(r.isSuccessful());
            }
            @Override public void onFailure(Call<Void> c, Throwable t) {
                live.postValue(false);
            }
        });
        return live;
    }

    /* ──────────────── API wrappers ──────────────── */

//    Get all the mails where the user is the sender or the receiver
    public LiveData<List<Mail>> getAllUserMails() {
        return toLiveData(webServiceAPI.getAllUserMails());
    }

//    Return the mail Obj by its ID
    public LiveData<Mail> getMail(String id) {
        return toLiveData(webServiceAPI.getMail(id));
    }
//    Return all the mail Obj that belong to the logged in user, that have the query
    public LiveData<List<Mail>> searchMails(String query) {
        return toLiveData(webServiceAPI.searchString(query));
    }
//    Create a new mail
    public LiveData<String> createMail(Mail mail) {
        MutableLiveData<String> live = new MutableLiveData<>();

        webServiceAPI.createMail(mail).enqueue(new Callback<>() {
            @Override public void onResponse(Call<String> c, Response<String> r) {
                if (r.isSuccessful() && r.body() != null && !r.body().isEmpty()) {
                    String cleanId = r.body().replace("\"", "").trim();
                    live.postValue(cleanId);
                } else {
                    live.postValue(null);
                }
            }
            @Override public void onFailure(Call<String> c, Throwable t) {
                live.postValue(null);
            }
        });
        return live;
    }
//    Create a new draft
    public LiveData<String> createDraft(Mail mail) {
        MutableLiveData<String> live = new MutableLiveData<>();

        webServiceAPI.createDraft(mail).enqueue(new Callback<Mail>() {
            @Override public void onResponse(Call<Mail> c, Response<Mail> r) {
                if (r.isSuccessful() && r.body() != null) {
                    String id = r.body().getId();
                    live.postValue(id);
                } else {
                    live.postValue(null);
                }
            }
            @Override public void onFailure(Call<Mail> c, Throwable t) {
                live.postValue(null);
            }
        });
        return live;
    }

//  Edit a draft
    public LiveData<Boolean> editDraft( String mailID, Mail mail) {
        return toBoolLiveData(webServiceAPI.editDraft(mailID, mail));
    }

//    Edit a mail
    public LiveData<Boolean> editMail(String id, Mail mail) {
        return toBoolLiveData(webServiceAPI.editMail(id, mail));
    }

//    Delete the mail/draft
    public LiveData<Boolean> deleteMail(String id) {
        return toBoolLiveData(webServiceAPI.deleteMail(id));
    }

}


