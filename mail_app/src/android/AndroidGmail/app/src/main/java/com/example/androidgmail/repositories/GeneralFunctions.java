package com.example.androidgmail.repositories;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import android.os.Handler;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.entities.Mail;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    Helper class that uses all 3 repositories for tasks that need all three (user, mail, label)
    in order to keep the viewModel logic free
 */
public class GeneralFunctions {
    private MailsRepository mailsRepo;

    private UserRepository usersRepo;

    private LabelRepository labelRepo;


/*---------Constructors-------------*/
    public GeneralFunctions(Context ctx) {
        this.mailsRepo = new MailsRepository(ctx);
        this.usersRepo = new UserRepository(ctx);
        this.labelRepo = new LabelRepository(ctx);
    }

    public GeneralFunctions(Context ctx, LabelRepository labelRepo) {
        this.mailsRepo = new MailsRepository(ctx);
        this.usersRepo = new UserRepository(ctx);
        this.labelRepo = labelRepo;
    }


/*-----------Functions-------------*/

    public LiveData<String> loginAndInitialize(Map<String, String> creds) {

//        Add the new user to userDAO
        LiveData<String> userSrc = usersRepo.addUser(creds);

        MediatorLiveData<String> loginResult = new MediatorLiveData<>();

        loginResult.addSource(userSrc, ok -> {
//          Add the new user's label to labelDAO
            if (ok.equals("Login Successful")) {
//          Initialize mails and labels in room
                reloadMailsAndLabels();
            }
            loginResult.setValue(ok);
        });
        return loginResult;
    }



    public void reloadMailsAndLabels() {
        labelRepo.reload();

        LiveData<List<Label>> labelsLD = labelRepo.getAll();

        labelsLD.observeForever(new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {

                if (labels == null || labels.isEmpty()) {
                    return;
                }

                new Thread(() -> {
                    Map<String, String> defaults = usersRepo.getUserDefaultLabels();
                    if (defaults == null) return;

                    String draftId  = defaults.get("draft");
                    String spamId   = defaults.get("spam");
                    String trashId  = defaults.get("trash");
                    String starredId = defaults.get("starred");

                    List<String> draftIDs  = findIds(labels, draftId);
                    List<String> spamIDs   = findIds(labels, spamId);
                    List<String> trashIDs  = findIds(labels, trashId);
                    List<String> starredIDs  = findIds(labels, starredId);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        labelsLD.removeObserver(this);
                        mailsRepo.reload(draftIDs, spamIDs, trashIDs, starredIDs);
                    });

                }).start();
            }
        });
    }

// Reload mails and labels helper function
    @NonNull
    private List<String> findIds(List<Label> labels, String wantedId) {
        if (wantedId == null) return Collections.emptyList();

        for (Label l : labels) {
            if (wantedId.equals(l.getId())) {
                return new ArrayList<>(l.getMails());
            }
        }
        Log.w("findIds", "label " + wantedId + " not found!");
        return Collections.emptyList();
    }


//   Updates the Room labels when creating a new mail
    public LiveData<String> addMail(Mail mail, boolean isDraft) {
        LiveData<String> mailID = mailsRepo.addMail(mail, isDraft);
        labelRepo.reload();
        return mailID;
    }


//   Updates the Room labels when editing a new mail
    public LiveData<Boolean> updateMail(String mailID, Mail mail, boolean isDraft) {
        LiveData<Boolean> updated = mailsRepo.updateMail(mailID, mail, isDraft);
        labelRepo.reload();
        return updated;
    }


//   Updates the Room labels when deleting a new mail
    public LiveData<Boolean> deleteMail(String mailID) {
        LiveData<Boolean> deleted = mailsRepo.deleteMail(mailID);
        labelRepo.reload();
        return deleted;
    }


    public LiveData<List<Label>> getCustomLabels() {
        labelRepo.reload();
        LiveData<List<Label>> allLabels = labelRepo.getAll();   // Room LiveData
        MediatorLiveData<List<Label>> out = new MediatorLiveData<>();

        ExecutorService DB_IO = Executors.newSingleThreadExecutor();
        out.addSource(allLabels, labels -> {
            if (labels == null) {
                out.setValue(Collections.emptyList());
                return;
            }

            DB_IO.execute(() -> {
                // get user default labels
                Map<String,String> defaults = usersRepo.getUserDefaultLabels();
                if (defaults == null) defaults = Collections.emptyMap();

                Set<String> systemIds = new HashSet<>(defaults.values());

                // Removes all the default user labels from the list of all labels
                List<Label> custom = new ArrayList<>();
                for (Label l : labels) {
                    if (!systemIds.contains(l.getId())) {
                        custom.add(l);
                    }
                }

                out.postValue(custom);
            });
        });

        return out;
    }



    // Returns a list of mails that belong to the mailID's list in a label obj
    public LiveData<List<Mail>> getMailsFromDefaultLabel(String labelName) {
        LiveData<String> labelID = usersRepo.getUserDefaultLabel(labelName);

        MediatorLiveData<List<Mail>> result = new MediatorLiveData<>();

        result.addSource(labelID, id -> {
            result.removeSource(labelID);

            if (id == null) {
                result.postValue(Collections.emptyList());
            } else {
                LiveData<List<Mail>> labelMails = getMailsFromLabel(id);
                result.addSource(labelMails, result::postValue);
            }
        });
        return result;
    }

    public LiveData<List<Mail>> getMailsFromLabel(String labelID) {
        LiveData<Label> labelLive = labelRepo.getByID(labelID);
        LiveData<List<Mail>> allMailsLive = mailsRepo.getAllCachedMails();

        MediatorLiveData<List<Mail>> filteredMails = new MediatorLiveData<>();

        // We need to store latest values of label and mails to combine them
        final Label[] latestLabel = new Label[1];
        final List<Mail>[] latestMails = new List[1];

        filteredMails.addSource(labelLive, labelObj -> {
            latestLabel[0] = labelObj;
            combineAndPost(latestLabel[0], latestMails[0], filteredMails);
        });

        filteredMails.addSource(allMailsLive, mailsList -> {
            latestMails[0] = mailsList;
            combineAndPost(latestLabel[0], latestMails[0], filteredMails);
        });

        return filteredMails;
    }

//    Helper function for get mails from label
    private void combineAndPost(Label label, List<Mail> mails, MediatorLiveData<List<Mail>> result) {
        if (label == null || mails == null) {
            result.postValue(Collections.emptyList());
            return;
        }

        List<String> mailIdsInLabel = label.getMails();
        if (mailIdsInLabel == null || mailIdsInLabel.isEmpty()) {
            result.postValue(Collections.emptyList());
            return;
        }

        List<Mail> filtered = new ArrayList<>();
        for (Mail mail : mails) {
            if (mailIdsInLabel.contains(mail.getId())) {
                filtered.add(mail);
            }
        }

        result.postValue(filtered);
    }



    public LiveData<Boolean> addOrRemoveFromDefaultLabel(
            String labelName, Mail mail, boolean add) {

        LiveData<String> labelID = usersRepo.getUserDefaultLabel(labelName);
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        result.addSource(labelID, id -> {
            result.removeSource(labelID);
            if (id == null) {
                result.postValue(false);
                return;
            }

            LiveData<Boolean> op = add
                    ? labelRepo.addMailToLabel(id, mail.getId())
                    : labelRepo.removeMailFromLabel(id, mail.getId());

            result.addSource(op, ok -> {
                result.removeSource(op);

                if (Boolean.TRUE.equals(ok)) {
                    mailsRepo.updateMailDAO(mail);
                }

                result.postValue(Boolean.TRUE.equals(ok));
            });
        });

        return result;
    }

    public LiveData<Boolean> addOrRemoveFromCustomLabel( String labelID, String mailID) {
        LiveData<Label> label = labelRepo.getByID(labelID);
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        result.addSource(label, l -> {
            result.removeSource(label);

            if (label == null) {
                result.postValue(false);
                return;
            }

            boolean add = !l.getMails().contains(mailID);

            LiveData<Boolean> op = add
                    ? labelRepo.addMailToLabel(labelID, mailID)
                    : labelRepo.removeMailFromLabel(labelID, mailID);

            result.addSource(op, ok -> {
                result.removeSource(op);
                result.postValue(Boolean.TRUE.equals(ok));
            });
        });

        return result;
    }

}
