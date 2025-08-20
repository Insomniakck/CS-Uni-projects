package com.example.androidgmail.api;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.androidgmail.BuildConfig;
import com.example.androidgmail.entities.User;
import com.example.androidgmail.repositories.SessionManager;

import org.json.JSONObject;

import java.io.File;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UserAPI {
    private final WebServiceAPI api;

    public UserAPI(Context ctx) {

        // Attach header to every request
        OkHttpClient ok = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder rb = chain.request().newBuilder();

                    String uid = SessionManager.getUserId(ctx);
                    if (uid != null && !uid.isEmpty()) {
                        rb.header("X-User-Id", uid);
                    }

                    return chain.proceed(rb.build());
                })
                .build();

        // Call the backend server
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + BuildConfig.SERVER_IP + ":8080/api/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(ok)
                .build();

        api = retrofit.create(WebServiceAPI.class);
    }

    /* ──────────── tiny helpers to avoid repetition ──────────── */
    private <T> LiveData<T> toLiveData(Call<T> call) {
        MutableLiveData<T> live = new MutableLiveData<>();
        call.enqueue(new Callback<>() {
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
        call.enqueue(new Callback<>() {
            @Override public void onResponse(Call<Void> c, Response<Void> r) {
                live.postValue(r.isSuccessful());
            }
            @Override public void onFailure(Call<Void> c, Throwable t) {
                live.postValue(false);
            }
        });
        return live;
    }

    /* ──────────── public API wrappers ──────────── */

    public LiveData<String> createUser(File profileImage, String firstName, String lastName, String birthday, String username, String password) {
        MutableLiveData<String> live = new MutableLiveData<>();

        MultipartBody.Part imagePart = null;
        if (profileImage != null) {
            String fileName = profileImage.getName();
            String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            String mediaType = "image/" + extension;
            RequestBody requestFile = RequestBody.create(MediaType.parse(mediaType), profileImage);
            imagePart = MultipartBody.Part.createFormData("profileImage", fileName, requestFile);
        }

        RequestBody firstNameBody = RequestBody.create(MultipartBody.FORM, firstName);
        RequestBody lastNameBody  = RequestBody.create(MultipartBody.FORM, lastName);
        RequestBody birthdayBody  = RequestBody.create(MultipartBody.FORM, birthday);
        RequestBody usernameBody  = RequestBody.create(MultipartBody.FORM, username);
        RequestBody passwordBody  = RequestBody.create(MultipartBody.FORM, password);

        api.createUser(imagePart, firstNameBody, lastNameBody, birthdayBody, usernameBody, passwordBody)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            live.postValue("success");
                        } else {
                            try {
                                String errorJson = response.errorBody().string();
                                JSONObject json = new JSONObject(errorJson);
                                String errorMessage = json.optString("error", "Unknown error");
                                live.postValue(errorMessage);
                            } catch (Exception e) {
                                live.postValue("Unknown error");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        live.postValue("Network error");
                    }
                });

        return live;
    }


    // Returns the user's username, first name, last name, and profileImage
    public LiveData<User> getUserInfo(String username) {
        return toLiveData(api.getUserInfo(username));
    }

    public LiveData<User> getUserByID(String id) {
        return toLiveData(api.getUserByID(id));
    }

    public LiveData<String> loginUser(Map<String, String> creds) {
        MutableLiveData<String> live = new MutableLiveData<>();

        api.loginUser(creds).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<String> c, Response<String> r) {
                if (r.isSuccessful() && r.body() != null && !r.body().isEmpty()) {
                    live.postValue(r.body().replace("\"", "").trim());
                } else {
                    try {
                        String errorJson = r.errorBody().string();
                        JSONObject obj = new JSONObject(errorJson);
                        String errorMsg = obj.optString("error", "Unknown error");
                        live.postValue(errorMsg);
                    } catch (Exception e) {
                        live.postValue("Error parsing error response");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> c, Throwable t) {
                live.postValue("Network error: " + t.getMessage());
            }
        });

        return live;
    }


    public LiveData<Map<String,String>> getUserDefaultLabels() {
        return toLiveData(api.getUserDefaultLabels());
    }

    public LiveData<String> getUserDefaultLabelsByName(String labelName) {
        MutableLiveData<String> live = new MutableLiveData<>();

        Map<String, String> body = Map.of("labelName", labelName);

        api.getUserDefaultLabelsByName(body).enqueue(new Callback<>() {
            @Override public void onResponse(Call<String> c, Response<String> r) {
                if (r.isSuccessful() && r.body() != null && !r.body().isEmpty()) {
                    live.postValue(r.body().replace("\"", "").trim());
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

}


