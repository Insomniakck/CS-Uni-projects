package com.example.androidgmail.api;
import com.example.androidgmail.BuildConfig;

import android.content.Context;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.helpers.LabelMailBody;
import com.example.androidgmail.repositories.SessionManager;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LabelAPI {

    Retrofit retrofit;
    WebServiceAPI webServiceAPI;

    public LabelAPI(Context ctx) {

        // Attach header to every request
        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("X-User-Id", SessionManager.getUserId(ctx))
                                .build()))
                .build();

        // Call the backend server
        retrofit = new Retrofit.Builder()
                .baseUrl("http://"  + BuildConfig.SERVER_IP + ":8080/api/")
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

// Returns all the labels that belong to the logged in user
    public LiveData<List<Label>> getAllLabels() {

        return toLiveData(webServiceAPI.getAllLabels());
    }


// Creates a label in the mongoDB server
    public LiveData<String> createLabel(Label label) {
        MutableLiveData<String> live = new MutableLiveData<>();

        webServiceAPI.createLabel(label).enqueue(new Callback<>() {
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

// Returns the label with the corresponding ID
    public LiveData<Label> getLabel(String id) {
        return toLiveData(webServiceAPI.getLabel(id));
    }

// Edits an existing label
    public LiveData<Boolean> editLabel(String labelID, Label label){
        return toBoolLiveData(webServiceAPI.editLabel(labelID, label));
    }

// Deletes an existing label
    public LiveData<Boolean> deleteLabel(String labelID) {
        return toBoolLiveData(webServiceAPI.deleteLabel(labelID));
    }

// Adds a mailID to the mailID list inside a label object
    public LiveData<Boolean> addMailToLabel(String labelID, String mailID) {
        LabelMailBody ids = new LabelMailBody(labelID, mailID);
        return toBoolLiveData(webServiceAPI.addMailToLabel(ids));
    }

// Removes a mailID from the mailID list inside a label object
    public LiveData<Boolean>  removeMailFromLabel(String labelID, String mailID) {
        LabelMailBody ids = new LabelMailBody(labelID, mailID);
        return toBoolLiveData(webServiceAPI.removeMailFromLabel(ids));
    }

// Returns the mails whose mailID's appear in the label's mailID list
    public LiveData<List<Mail>> getMailsFromLabel(String labelID) {

        MutableLiveData<List<Mail>> out = new MutableLiveData<>();

        webServiceAPI.getMailsForLabel(labelID)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<Map<String,Object>> c,
                                           Response<Map<String,Object>> r) {

                        Object raw = r.body() == null ? null : r.body().get("mails");
                        List<Mail> list;

                        if (raw instanceof List<?>) {
                            List<?> arr = (List<?>) raw;

                            Gson g = new Gson();
                            List<Mail> tmp = new ArrayList<>(arr.size());
                            for (Object o : arr) {
                                tmp.add(g.fromJson(g.toJson(o), Mail.class));
                            }
                            list = tmp;
                        } else {
                            list = Collections.emptyList();
                        }

                        out.postValue(list);
                    }

                    @Override
                    public void onFailure(Call<Map<String,Object>> c, Throwable t) {
                        out.postValue(Collections.emptyList());
                    }
                });

        return out;
    }
}
