package com.example.androidgmail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.viewmodels.MailsViewModel;
import com.example.androidgmail.viewmodels.UserViewModel;


public class ComposeMail extends AppCompatActivity {

    public static final String EXTRA_DRAFT_ID = "mail_id";

    private MailsViewModel mailsViewModel;
    private UserViewModel userViewModel;


    private void goHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_mail);

        mailsViewModel = new ViewModelProvider(this).get(MailsViewModel.class);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);



        Button sendButton = findViewById(R.id.buttonSend);
        EditText receiverField = findViewById(R.id.editTextTo);
        EditText subjectField = findViewById(R.id.editTextSubject);
        EditText contentField = findViewById(R.id.editTextBody);
        ImageButton buttonClose = findViewById(R.id.buttonClose);



        String draftId = getIntent().getStringExtra(EXTRA_DRAFT_ID);
        if (draftId != null) {
            mailsViewModel.getMail(draftId).observe(this, mail -> {
                if (mail == null) return;
                receiverField.setText(mail.getReceiver());
                subjectField .setText(mail.getSubject());
                contentField .setText(mail.getContent());
            });
        }


        buttonClose.setOnClickListener(v -> {
            userViewModel.getLoggedInUser().observe(this, user -> {
                if (user != null) {
                    String sender = user.getUsername();
                    String receiver = receiverField.getText().toString().trim();
                    String subject = subjectField.getText().toString();
                    String content = contentField.getText().toString();

                    if (receiver.isEmpty() && subject.isEmpty() && content.isEmpty() && draftId == null) {
                        Toast.makeText(this, "Discarded draft", Toast.LENGTH_SHORT).show();
                        finish(); // closes ComposeMailActivity and returns to MainActivity
                    } else {
                        Mail newMail = new Mail(sender, receiver, subject, content);



                        if (draftId != null) {
                            mailsViewModel.updateMail(draftId, newMail,
                                            /*isDraft=*/ true)
                                    .observe(this, ok -> {
                                        Toast.makeText(this, "Updated draft", Toast.LENGTH_SHORT).show();
                                        goHome();
                                    });
                        } else {
                            mailsViewModel.addMail(newMail,
                                            /*isDraft=*/ true)
                                    .observe(this, id -> {
                                        Toast.makeText(this, "Created draft", Toast.LENGTH_SHORT).show();
                                        goHome();
                                    });
                        }

                    }
                }
            });
        });


        sendButton.setOnClickListener( v -> {
            userViewModel.getLoggedInUser().observe(this, user -> {
                if (user != null) {
                    String sender = user.getUsername();
                    String receiver = receiverField.getText().toString().trim();
                    String subject = subjectField.getText().toString();
                    String content = contentField.getText().toString();

                    if (receiver.isEmpty() || subject.isEmpty() || content.isEmpty()) {
                        Toast.makeText(this, "One or more fields missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Mail newMail = new Mail(sender, receiver, subject, content);

                    if (draftId != null) {
                        mailsViewModel.updateMail(draftId, newMail,
                                        /*isDraft=*/ false)
                                .observe(this, ok -> {
                                    Toast.makeText(this, "Draft sent successfully", Toast.LENGTH_SHORT).show();
                                    goHome();
                                });
                    } else {
                        mailsViewModel.addMail(newMail,
                                        /*isDraft=*/ false)
                                .observe(this, id -> {
                                    Toast.makeText(this, "Mail sent successfully", Toast.LENGTH_SHORT).show();
                                    goHome();
                                });
                    }
                }
            });
        });
    }
}
