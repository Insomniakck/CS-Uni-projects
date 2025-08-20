package com.example.androidgmail.repositories;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.example.androidgmail.api.UserAPI;
import com.example.androidgmail.dao.LocalDatabase;
import com.example.androidgmail.dao.UserDao;
import com.example.androidgmail.entities.User;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

public class UserRepository {
    private final Context appCtx;
    private UserDao userDao;
    private UserData userData;

    private UserAPI api;

    public UserRepository(Context ctx){
        this.appCtx = ctx.getApplicationContext();
        LocalDatabase db = LocalDatabase.getInstance(appCtx);
        userDao = db.userDao();
        userData = new UserData();
        api = new UserAPI(appCtx);
    }

    class UserData extends MutableLiveData<User> {

        public UserData(){
            super();
        }
    }

//    =========DAO=============================
    /** Log in and cache the user in Room */
    public LiveData<String> addUser(Map<String,String> credentials) {

        MediatorLiveData<String> result = new MediatorLiveData<>();

        LiveData<String> idSrc = loginUser(credentials);
        result.addSource(idSrc, id -> {
            result.removeSource(idSrc);

            // Reject known error messages
            Set<String> knownErrors = new HashSet<>(Arrays.asList(
                    "Incorrect Username.",
                    "Incorrect Password.",
                    "Username or Password missing."
            ));

            if (id == null || knownErrors.contains(id)) {
                result.postValue(id);
                return;
            }

            LiveData<User> userSrc = api.getUserByID(id);
            result.addSource(userSrc, user -> {
                result.removeSource(userSrc);
                if (user == null) {    // network error
                    result.postValue("Network Error");
                    return;
                }

                LiveData<Map<String,String>> lblSrc = api.getUserDefaultLabels();
                result.addSource(lblSrc, labels -> {
                    result.removeSource(lblSrc);
                    if (labels == null) {
                        result.postValue("Network Error");
                        return;
                    }

                    user.setId(id);
                    user.setDefaultLabels(labels);

                    // store locally on a bg thread
                    new Thread(() -> userDao.insert(user)).start();

                    result.postValue("Login Successful");
                });
            });
        });

        return result;
    }



    public LiveData<String> getUserDefaultLabel(String labelName) {

        MediatorLiveData<String> result = new MediatorLiveData<>();

        LiveData<User> userSrc = userDao.get(SessionManager.getUserId(appCtx));

        result.addSource(userSrc, user -> {
            result.removeSource(userSrc);
            if (user == null) {
                result.setValue(null);
            } else {
                result.setValue(user.getLabelID(labelName));
            }
        });

        return result;
    }

    public Map<String,String> getUserDefaultLabels() {
        User user = userDao.getSync(SessionManager.getUserId(appCtx));
        return user != null ? user.getDefaultLabels() : null;
    }



    public LiveData<User> getLoggedInUser() {
        return userDao.get(SessionManager.getUserId(appCtx));
    }


    public LiveData<List<User>> getAllLoggedUsers() {
        return userDao.getAll();
    }


    public void removeUser(String userID) {
        Executors.newSingleThreadExecutor().execute(() -> userDao.delete(userID));
    }




//    =======================API======================================


    public LiveData<String> createUser(File profileImage, String firstName, String lastName, String birthday, String username, String password) {
        return api.createUser(profileImage, firstName, lastName, birthday, username, password);
    }


    /*
        If the username and password are correct,
        return true  and add the userID to the sessionManager
     */
    private LiveData<String> loginUser(Map<String, String> credentials) {
        MutableLiveData<String> result = new MutableLiveData<>();
        LiveData<String> apiSrc = api.loginUser(credentials);

        Observer<String> obs = new Observer<String>() {
            @Override
            public void onChanged(String response) {
                apiSrc.removeObserver(this);

                if (response != null) {
                    String trimmed = response.replace("\"", "").trim();

                    // List of known error messages
                    Set<String> knownErrors = new HashSet<>(Arrays.asList(
                            "Incorrect Username.",
                            "Incorrect Password.",
                            "Username or Password missing."
                    ));

                    if (!knownErrors.contains(trimmed)) {
                        SessionManager.setUserId(appCtx, trimmed);
                    }

                    result.postValue(trimmed);
                } else {
                    result.postValue("Unknown error occurred");
                }
            }
        };

        apiSrc.observeForever(obs);
        return result;
    }


}
