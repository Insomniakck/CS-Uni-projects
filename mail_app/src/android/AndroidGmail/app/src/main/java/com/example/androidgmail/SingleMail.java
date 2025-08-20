package com.example.androidgmail;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.ui.LabelPickerPopup;
import com.example.androidgmail.viewmodels.LabelViewModel;
import com.example.androidgmail.viewmodels.MailsViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.Objects;

public class SingleMail extends AppCompatActivity {

    public static final String EXTRA_MAIL_ID = "mail_id";

    private MailsViewModel mailViewModel;
    private LiveData<Mail> mailLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_mail);

        String mailId = getIntent().getStringExtra(EXTRA_MAIL_ID);
        if (mailId == null) {
            finish();
            return;
        }

        /* ---------- Toolbar ---------- */
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        toolbar.setNavigationOnClickListener(v -> {
            mailViewModel.reload();
            finish();
        });

        /* ---------- Views ---------- */
        ImageView avatar = findViewById(R.id.imgAvatar);
        TextView sender = findViewById(R.id.txtSender);
        TextView subject = findViewById(R.id.txtSubject);
        TextView content = findViewById(R.id.txtContent);
        TextView dateTxt = findViewById(R.id.txtDate);
        ImageView btnStar = findViewById(R.id.btnStar);
        ImageView btnSpam = findViewById(R.id.btnSpam);
        ImageView btnTrash = findViewById(R.id.btnTrash);
        ImageView btnLabel = findViewById(R.id.btnLabel);


        mailViewModel = new ViewModelProvider(this).get(MailsViewModel.class);
        mailLive = mailViewModel.getMail(mailId);


        LabelViewModel labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        LiveData<List<Label>> userLabels = labelViewModel.getCustomLabels();

        mailLive.observe(this, mail -> {
            if (mail == null) return;

            // basic fields
            String senderName = mail.getSenderFirstName() != null
                    ? mail.getSenderFirstName() + " " + mail.getSenderLastName()
                    : mail.getSender();
            sender.setText(senderName);
            subject.setText(mail.getSubject());
            content.setText(mail.getContent());
            if (mail.getDate() != null) dateTxt.setText(mail.getParsedDate());

            String imageUrl = mail.getSenderProfileImage();

            if (imageUrl != null && !imageUrl.isEmpty()) {

                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .circleCrop()
                        .into(avatar);
            } else {
                avatar.setImageResource(R.drawable.default_profile); // fallback
            }


            //  initial star icon
            btnStar.setImageResource(
                    mail.getStar() ? R.drawable.ic_star_filled : R.drawable.ic_star);
            // initial spam icon
            btnSpam.setImageResource(
                    mail.getSpam() ? R.drawable.ic_spam_filled : R.drawable.ic_spam);
            //  initial trash icon
            btnTrash.setImageResource(
                    mail.getTrash() ? R.drawable.ic_trash_filled : R.drawable.ic_trash);


        });

        /* ---------- Star toggle ---------- */
        btnStar.setOnClickListener(v -> {
            Mail current = mailLive.getValue();
            if (current == null) return;

            boolean newState = !current.getStar();
            current.setStar(newState);

            btnStar.setImageResource(
                    newState ? R.drawable.ic_star_filled : R.drawable.ic_star);

            // tell ViewModel to add/remove the mail from the "starred" default‑label
            mailViewModel.addOrRemoveFromDefaultLabel("starred",
                            current,
                            newState)
                    .observe(this, ok -> {
                        Toast.makeText(this, "Mail " + (newState ? "starred" : "unstarred"), Toast.LENGTH_SHORT).show();

                    });
        });

        /* ---------- Spam toggle ---------- */
        btnSpam.setOnClickListener(v -> {
            Mail current = mailLive.getValue();
            if (current == null) return;

            boolean newState = !current.getSpam();
            current.setSpam(newState);

            btnSpam.setImageResource(
                    newState ? R.drawable.ic_spam_filled : R.drawable.ic_spam);

            // tell ViewModel to add/remove the mail from the "spam" default‑label
            mailViewModel.addOrRemoveFromDefaultLabel("spam",
                            current,
                            newState)
                    .observe(this, ok -> {
                        Toast.makeText(this, "Mail " + (newState ? "added to spam" : "removed from spam"), Toast.LENGTH_SHORT).show();
                    });
        });
        /* ---------- Trash toggle ---------- */
        btnTrash.setOnClickListener(v -> {
            Mail current = mailLive.getValue();
            if (current == null) return;

            boolean newState = !current.getTrash();
            current.setTrash(newState);

            btnTrash.setImageResource(
                    newState ? R.drawable.ic_trash_filled : R.drawable.ic_trash);

            mailViewModel.addOrRemoveFromDefaultLabel("trash",
                            current,
                            newState)
                    .observe(this, ok -> {
                        Toast.makeText(this, "Mail " + (newState ? "added to trash" : "removed from trash"), Toast.LENGTH_SHORT).show();
                    });
        });

        /* ---------- Label button ---------- */
        btnLabel.setOnClickListener(btn -> {
            new LabelPickerPopup(
                    this,
                    this,
                    labelViewModel.getCustomLabels(),
                    label -> {
                        Mail m = mailLive.getValue();
                        if (m != null) {
                            mailViewModel.addOrRemoveFromCustomLabel(label.getId(), m.getId())
                                    .observe(this, ok -> {});
                        }
                    },
                    mailLive.getValue().getId()
            ).show(btn);
        });


    }
}
