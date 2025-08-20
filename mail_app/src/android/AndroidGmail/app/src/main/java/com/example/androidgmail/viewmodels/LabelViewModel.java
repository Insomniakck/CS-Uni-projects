package com.example.androidgmail.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.repositories.GeneralFunctions;
import com.example.androidgmail.repositories.LabelRepository;
import java.util.List;

public class LabelViewModel extends AndroidViewModel {
    private LabelRepository labelRepository;
    private LiveData<List<Label>> labels;
    private GeneralFunctions labelFuncs;

    public LabelViewModel(@NonNull Application application) {
        super(application);
        labelRepository = new LabelRepository(application.getApplicationContext());
        labelFuncs = new GeneralFunctions(application.getApplicationContext(), labelRepository);
    }

    public LiveData<Label> getByID(String labelID) {
        return labelRepository.getByID(labelID);
    }

    public LiveData<List<Label>> getAll() {
        return labelRepository.getAll();
    }

    public LiveData<List<Label>> getCustomLabels() {
        return labelFuncs.getCustomLabels();
    }

    public LiveData<String> addLabel(Label label) {
        return labelRepository.addLabel(label);
    }

    public LiveData<Boolean> editLabel(String labelID, Label label) {
        return labelRepository.editLabel(labelID, label);
    }

    public LiveData<Boolean> deleteLabel(String labelID) {
        return labelRepository.deleteLabel(labelID);
    }

    public LiveData<Boolean> addMailToLabel(String labelID, String mailID) {
        return labelRepository.addMailToLabel(labelID, mailID);
    }

    public LiveData<Boolean> removeMailFromLabel(String labelID, String mailID) {
        return labelRepository.removeMailFromLabel(labelID, mailID);
    }

    public void reload() {
        labelRepository.reload();
    }
}

