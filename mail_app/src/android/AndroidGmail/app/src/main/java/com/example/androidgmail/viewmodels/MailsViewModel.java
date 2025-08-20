package com.example.androidgmail.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.repositories.GeneralFunctions;
import com.example.androidgmail.repositories.MailsRepository;

import java.util.List;

public class MailsViewModel extends AndroidViewModel {
    private MailsRepository mailsRepository;
    private LiveData<List<Mail>> mails;

    private GeneralFunctions mailFunctions;

    public MailsViewModel(@NonNull Application application) {
        super(application);
        this.mailFunctions = new GeneralFunctions(application.getApplicationContext());
        mailsRepository = new MailsRepository(application.getApplicationContext());
    }

    public LiveData<Mail> getMail(String mailID) {
        return mailsRepository.getMail( mailID );
    }

    public LiveData<List<Mail>> getMails(List<String> idList) {
        return mailsRepository.getMails( idList );
    }

    public LiveData<String> addMail(Mail mail, boolean isDraft) {
        return mailFunctions.addMail( mail, isDraft );
    }

    public LiveData<Boolean> updateMail(String mailID, Mail mail, boolean isDraft) {
        return mailFunctions.updateMail( mailID, mail, isDraft );
    }

    public LiveData<Boolean> deleteMail(String mailID) {
        return mailFunctions.deleteMail( mailID );
    }

    public LiveData<List<Mail>> queryMails(String query) {
        return mailsRepository.queryMails( query );
    }

    public LiveData<List<Mail>> getMailsFromDefaultLabel(String labelName) {
        return mailFunctions.getMailsFromDefaultLabel( labelName );
    }

    public LiveData<List<Mail>> getMailsFromLabel(String labelID) {
        return mailFunctions.getMailsFromLabel( labelID );
    }

    public LiveData<Boolean> addOrRemoveFromDefaultLabel( String labelName, Mail mail, boolean add){
        return mailFunctions.addOrRemoveFromDefaultLabel(labelName, mail, add);
    }

    public LiveData<Boolean> addOrRemoveFromCustomLabel( String labelID, String mailID) {
        return mailFunctions.addOrRemoveFromCustomLabel( labelID, mailID);
    }

    public void reload() {
        mailFunctions.reloadMailsAndLabels();
    }
}