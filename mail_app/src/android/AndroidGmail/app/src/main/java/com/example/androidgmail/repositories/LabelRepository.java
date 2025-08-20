package com.example.androidgmail.repositories;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.example.androidgmail.api.LabelAPI;
import com.example.androidgmail.dao.LabelDao;
import com.example.androidgmail.dao.LocalDatabase;
import com.example.androidgmail.dao.UserDao;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.entities.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LabelRepository {

    private LabelDao labelDao;

    private UserDao userDao;

    private LabelListData labelListData;

    private LabelAPI labelAPI;

    private Context ctx;

    private static final Executor DB_IO = Executors.newSingleThreadExecutor();

    /* in‑memory LiveData cache */
    private final LabelListData labelCache;

    public LabelRepository(Context ctx){
        this.ctx = ctx;
        LocalDatabase db = LocalDatabase.getInstance(ctx);
        labelDao = db.labelDao();
        userDao = db.userDao();
        labelListData = new LabelRepository.LabelListData();
        labelAPI = new LabelAPI(ctx);
        labelCache = new LabelListData();
    }

    class LabelListData extends MutableLiveData<List<Label>> {

        private boolean loaded = false;

        @Override protected void onActive() {
            super.onActive();
            if (loaded) return;                    // already loaded once
            loaded = true;

            DB_IO.execute(() -> {
                User user = userDao.getSync(SessionManager.getUserId(ctx)); // blocking helper
                if (user == null) return;
                List<Label> rows = labelDao.getByUsernameSync(user.getUsername());
                postValue(rows);
            });
        }
    }

    public LiveData<List<Label>> getCachedLabels() { return labelCache; }

    //    ============DAO================
    public LiveData<Label> getByID(String labelID) {
        LiveData<Label> apiSource = labelAPI.getLabel(labelID);

        apiSource.observeForever(new Observer<Label>() {
            @Override
            public void onChanged(Label fromAPI) {
                if (fromAPI == null) return;

                // Insert into Room
                new Thread(() -> labelDao.insert(fromAPI)).start();

                apiSource.removeObserver(this);
            }
        });

        return labelDao.get(labelID);
    }

    public List<String> getMailIDsFromLabel(String labelId) {
        Label label = labelDao.getSync(labelId);

        if (label == null || label.getMails() == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(label.getMails());
    }



    public LiveData<List<Label>> getAll() {
        return labelCache;
    }


    private void refreshCache() {
        String userId = SessionManager.getUserId(ctx);
        User user = userDao.getSync(userId);
        if (user == null) {
            Log.w("refreshCache", "User null for id " + userId);
            return;
        }

        List<Label> rows = labelDao.getByUsernameSync(user.getUsername());

        labelCache.postValue(new ArrayList<>(rows));
    }



/* ──────────────────────────────────────────────────────────
   ADD  LABEL
   ────────────────────────────────────────────────────────── */

    public LiveData<String> addLabel(Label label) {
        MediatorLiveData<String> res = new MediatorLiveData<>();
        LiveData<String> api = labelAPI.createLabel(label);
        res.addSource(api, id -> {
            res.removeSource(api);
            if (id == null || id.isEmpty()) { res.setValue(null); return; }
            label.setID(id);
            DB_IO.execute(() -> {
                labelDao.insert(label);
                refreshCache();
            });
            res.setValue(id);
        });
        return res;
    }

/* ──────────────────────────────────────────────────────────
   EDIT  LABEL
   ────────────────────────────────────────────────────────── */

    public LiveData<Boolean> editLabel(String labelID, Label label) {
        MediatorLiveData<Boolean> res = new MediatorLiveData<>();
        LiveData<Boolean> api = labelAPI.editLabel(labelID, label);
        res.addSource(api, ok -> {
            res.removeSource(api);
            if (Boolean.TRUE.equals(ok)) {
                DB_IO.execute(() -> {
                    labelDao.update(new Label(labelID,
                            label.getLabelName(), label.getUsername(), label.getMails()));
                    refreshCache();
                });
            }
            res.setValue(Boolean.TRUE.equals(ok));
        });
        return res;
    }

/* ──────────────────────────────────────────────────────────
   DELETE LABEL
   ────────────────────────────────────────────────────────── */

    public LiveData<Boolean> deleteLabel(String labelID) {
        MediatorLiveData<Boolean> res = new MediatorLiveData<>();
        LiveData<Boolean> api = labelAPI.deleteLabel(labelID);
        res.addSource(api, ok -> {
            res.removeSource(api);
            if (Boolean.TRUE.equals(ok)) {
                DB_IO.execute(() -> { labelDao.delete(labelID); refreshCache(); });
            }
            res.setValue(Boolean.TRUE.equals(ok));
        });
        return res;
    }


/*─────────────────────────────────────────────────────────────────────
    API
 ───────────────────────────────────────────────────────────────────────*/

    public LiveData<List<Mail>> getMailsFromLabel(String labelID) {
        return labelAPI.getMailsFromLabel(labelID);
    }

    public LiveData<Boolean> addMailToLabel(String labelID, String mailID) {

        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        LiveData<Label> labelLiveData = getByID(labelID);
        labelLiveData.observeForever(new Observer<Label>() {
            @Override public void onChanged(Label currentLabel) {
                labelLiveData.removeObserver(this);

                if (currentLabel == null) { result.postValue(false); return; }

                List<String> mails = new ArrayList<>(currentLabel.getMails());
                if (mails.contains(mailID)) { result.postValue(true); return; }
                mails.add(mailID);

                Label updatedLabel = new Label(
                        currentLabel.getId(),
                        currentLabel.getLabelName(),
                        currentLabel.getUsername(),
                        mails);

                LiveData<Boolean> apiSource = labelAPI.addMailToLabel(labelID, mailID);
                result.addSource(apiSource, success -> {
                    result.removeSource(apiSource);

                    if (!Boolean.TRUE.equals(success)) {        // server failed
                        result.setValue(false);
                        return;
                    }

                    // DAO write + cache refresh AFTER success
                    new Thread(() -> {
                        labelDao.update(updatedLabel);
                        refreshCache();
                    }).start();

                    result.setValue(true);
                });
            }
        });
        return result;
    }


    public LiveData<Boolean> removeMailFromLabel(String labelID, String mailID) {

        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        LiveData<Label> labelLiveData = getByID(labelID);
        labelLiveData.observeForever(new Observer<Label>() {
            @Override public void onChanged(Label currentLabel) {
                labelLiveData.removeObserver(this);

                if (currentLabel == null) { result.postValue(false); return; }

                List<String> mails = new ArrayList<>(currentLabel.getMails());
                if (!mails.remove(mailID)) { result.postValue(true); return; }

                Label updatedLabel = new Label(
                        currentLabel.getId(),
                        currentLabel.getLabelName(),
                        currentLabel.getUsername(),
                        mails);

                LiveData<Boolean> apiSource = labelAPI.removeMailFromLabel(labelID, mailID);
                result.addSource(apiSource, success -> {
                    result.removeSource(apiSource);

                    if (!Boolean.TRUE.equals(success)) {        // server failed
                        result.setValue(false);
                        return;
                    }

                    // DAO write + cache refresh AFTER success
                    new Thread(() -> {
                        labelDao.update(updatedLabel);
                        refreshCache();
                    }).start();

                    result.setValue(true);
                });
            }
        });
        return result;
    }

    public void reload() {
        LiveData<User> currentUser = userDao.get(SessionManager.getUserId(ctx));
        currentUser.observeForever(user -> {
            if (user == null) return;

            LiveData<List<Label>> apiSrc = labelAPI.getAllLabels();
            apiSrc.observeForever(fromAPI -> {
                if (fromAPI == null) return;
                DB_IO.execute(() -> {
                    labelDao.insertAll(fromAPI);
                    refreshCache(); // update labelCache
                });
            });
        });
    }



}
