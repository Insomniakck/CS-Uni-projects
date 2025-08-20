package com.example.androidgmail.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidgmail.entities.User;
import com.example.androidgmail.repositories.GeneralFunctions;
import com.example.androidgmail.repositories.UserRepository;
import java.io.File;
import java.util.List;
import java.util.Map;

public class UserViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private GeneralFunctions init;

    public UserViewModel(@NonNull Application application) {
        super(application);
        this.init = new GeneralFunctions(application.getApplicationContext());
        userRepository = new UserRepository(application.getApplicationContext());
    }


    public LiveData<String> createUser(File profileImage,
                                        String firstName,
                                        String lastName,
                                        String birthday,
                                        String username,
                                        String password) {
        return userRepository.createUser(profileImage, firstName, lastName, birthday, username, password);
    }


    public LiveData<String> loginAndInit(Map<String, String> credentials) {
        return init.loginAndInitialize(credentials);
    }

    public LiveData<String> getUserDefaultLabel(String labelName) {
        return userRepository.getUserDefaultLabel( labelName );
    }

    public LiveData<User> getLoggedInUser() {
        return userRepository.getLoggedInUser();
    }

    public LiveData<List<User>> getAllLoggedUsers() {
        return userRepository.getAllLoggedUsers();
    }

    public void removeUser(String userID) {
        userRepository.removeUser( userID );
    }


}
