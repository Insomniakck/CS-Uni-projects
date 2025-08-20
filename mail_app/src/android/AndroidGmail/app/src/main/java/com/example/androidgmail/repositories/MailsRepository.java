package com.example.androidgmail.repositories;

import android.content.Context;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.example.androidgmail.api.MailAPI;
import com.example.androidgmail.api.UserAPI;
import com.example.androidgmail.dao.LocalDatabase;
import com.example.androidgmail.dao.MailDao;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.entities.User;
import android.os.Handler;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MailsRepository {
    private final MailDao mailDao;
    private final MailAPI mailAPI;
    private final UserAPI userApi;
    private static final Executor DB_IO = Executors.newSingleThreadExecutor();
    private final MailListData mailCache;


    public MailsRepository(Context ctx) {
        LocalDatabase db = LocalDatabase.getInstance(ctx);
        mailDao = db.mailDao();
        mailAPI = new MailAPI(ctx);
        userApi = new UserAPI(ctx);
        mailCache = new MailListData();
    }

    class MailListData extends MutableLiveData<List<Mail>> {

        private boolean loaded = false;

        @Override protected void onActive() {
            super.onActive();
            if (loaded) return;                       // already loaded once
            loaded = true;

            DB_IO.execute(() -> postValue(mailDao.getAllSync()));
        }
    }


    public LiveData<List<Mail>> getAllCachedMails() {
        return mailCache;
    }



    // Helper method to enrich the mail with sender info asynchronously
    private void addSenderInfo(Mail mail, CountDownLatch latch) {
        LiveData<User> userSrc = userApi.getUserInfo(mail.getSender());

        userSrc.observeForever(new Observer<>() {
            @Override public void onChanged(User u) {
                userSrc.removeObserver(this);

                if (u != null) {
                    mail.setSenderFirstName(u.getFirstName());
                    mail.setSenderLastName (u.getLastName());
                    mail.setSenderProfileImage(u.getProfileImage());
                }
                latch.countDown();
            }
        });
    }


    public void updateMailDAO(Mail mail) {
        new Thread(() -> mailDao.update(mail)).start();
    }


//    ============DAO=============================


    public LiveData<Mail> getMail(String mailID) {

        LiveData<Mail> apiSource = mailAPI.getMail(mailID);

        apiSource.observeForever(new Observer<Mail>() {
            @Override
            public void onChanged(Mail fromAPI) {
                if (fromAPI == null) return; // network error: keep cache

                CountDownLatch latch = new CountDownLatch(1);

                addSenderInfo(fromAPI, latch);

                // one-shot observer
                apiSource.removeObserver(this);
            }
        });

        return mailDao.get(mailID);
    }

    public LiveData<List<Mail>> getMails(List<String> idList) {

        for (String id : idList) {
            getMail(id);
        }

        // Stream the cached rows for the entire list
        return mailDao.getList(idList);
    }



    public LiveData<String> addMail(Mail mail, boolean isDraft) {

        MediatorLiveData<String> result = new MediatorLiveData<>();

        LiveData<String> idLive = isDraft
                ? mailAPI.createDraft(mail)
                : mailAPI.createMail(mail);


        result.addSource(idLive, id -> {
            result.removeSource(idLive);

            if (id == null || id.isEmpty()) {    // API failed
                result.postValue(null);
                return;
            }

            mail.setId(id);

            CountDownLatch latch = new CountDownLatch(1);

            addSenderInfo(mail, latch);
            

            new Thread(() -> {
                mail.setDraft(isDraft);
                mailDao.insert(mail);
            }).start();

            result.postValue(id);
        });

        return result;
    }



    public LiveData<Boolean> updateMail(String mailID, Mail mail, boolean isDraft) {

        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        LiveData<Boolean> apiLive = isDraft
                ? mailAPI.editDraft(mailID, mail)
                : mailAPI.editMail(mailID, mail);

        result.addSource(apiLive, ok -> {
            result.removeSource(apiLive);

            if (Boolean.TRUE.equals(ok)) {
                Mail updated = new Mail(
                        mailID,
                        mail.getSender(),
                        mail.getReceiver(),
                        mail.getSubject(),
                        mail.getContent());
                new Thread(() -> {
                    updated.setDraft(isDraft);
                    mailDao.update(updated);
                }).start();
            }

            result.setValue(Boolean.TRUE.equals(ok));
        });

        return result;
    }



    public LiveData<Boolean> deleteMail(String mailID) {

        MediatorLiveData<Boolean> result = new MediatorLiveData<>();

        LiveData<Boolean> apiLive = mailAPI.deleteMail(mailID);
        result.addSource(apiLive, ok -> {
            result.removeSource(apiLive);

            if (Boolean.TRUE.equals(ok)) {
                new Thread(() -> mailDao.delete(mailID)).start();
            }
            result.setValue(Boolean.TRUE.equals(ok));
        });

        return result;
    }

    public LiveData<List<Mail>> queryMails(String query) {

        LiveData<List<Mail>> apiSource = mailAPI.searchMails(query);
        MediatorLiveData<List<Mail>> result = new MediatorLiveData<>();

        result.addSource(apiSource, mails -> {
            result.removeSource(apiSource);

            if (mails != null) {
                DB_IO.execute(() -> mailDao.insertAll(mails));
            }
        });

        // stream what Room already has that matches the query
        LiveData<List<Mail>> local = mailDao.search(query);
        result.addSource(local, result::postValue);

        return result;
    }



    public void reload(List<String> draftIDs,
                       List<String> spamIDs,
                       List<String> trashIDs,
                       List<String> starredIDs) {

        LiveData<List<Mail>> apiSrc = mailAPI.getAllUserMails();

        apiSrc.observeForever(new Observer<>() {
            @Override public void onChanged(List<Mail> mails) {
                apiSrc.removeObserver(this);
                if (mails == null || mails.isEmpty()) return;

                new Handler(Looper.getMainLooper()).post(() -> {

                    CountDownLatch latch = new CountDownLatch(mails.size());

                    for (Mail mail : mails) {

                        mail.setDraft(draftIDs.contains(mail.getId()));
                        mail.setSpam (spamIDs .contains(mail.getId()));
                        mail.setTrash(trashIDs.contains(mail.getId()));
                        mail.setStar (starredIDs.contains(mail.getId()));

                        addSenderInfo(mail, latch);
                    }

                    DB_IO.execute(() -> {
                        try { latch.await(); } catch (InterruptedException ignored) {}

                        mailDao.upsert(mails);
                        postCacheUpdate();
                    });
                });
            }
        });
    }


    /* helper to re‑emit the current table into the cache */
    private void postCacheUpdate() {
        List<Mail> fresh = mailDao.getAllSync();
        mailCache.postValue(fresh);
    }


}





