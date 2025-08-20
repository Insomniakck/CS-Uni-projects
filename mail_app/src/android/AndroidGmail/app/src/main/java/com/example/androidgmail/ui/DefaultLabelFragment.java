package com.example.androidgmail.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.androidgmail.ComposeMail;
import com.example.androidgmail.R;
import com.example.androidgmail.SearchableFragment;
import com.example.androidgmail.SingleMail;
import com.example.androidgmail.adapters.MailsAdapter;
import com.example.androidgmail.databinding.FragmentInboxBinding;
import com.example.androidgmail.entities.Mail;
import com.example.androidgmail.viewmodels.MailsViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultLabelFragment extends Fragment implements SearchableFragment {

    private FragmentInboxBinding binding;
    private MailsAdapter adapter;
    private MailsViewModel viewModel;

    private String labelName;

    /* ------------------------------------------------------------------ */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding  = FragmentInboxBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(MailsViewModel.class);

        Bundle args = getArguments();
        labelName = args != null ? args.getString("label", "inbox") : "inbox"; // fallback to inbox



        /* ---------- RecyclerView + adapter ----------------------------- */
        adapter = new MailsAdapter(this::openMail);
        binding.recyclerMails.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.recyclerMails.setAdapter(adapter);

        setupSwipe();
        observeMails();
        return binding.getRoot();
    }

    /* ------------------------------------------------------------------ */
    private void setupSwipe() {
        SwipeRefreshLayout swipe = (SwipeRefreshLayout) binding.getRoot();

        swipe.setColorSchemeResources(R.color.my_primary, R.color.my_secondary);

        swipe.setOnRefreshListener(() -> viewModel.reload());
    }


    /* ------------------------------------------------------------------ */
    private void observeMails() {
        SwipeRefreshLayout swipe = (SwipeRefreshLayout) binding.getRoot();

        viewModel.getMailsFromDefaultLabel(labelName)
                .observe(getViewLifecycleOwner(), mails -> {
                    swipe.setRefreshing(false);

                    boolean isTrash = labelName.equals("trash");
                    boolean isSpam = labelName.equals("spam");

                    List<Mail> clean = new ArrayList<>(mails.size());
                    for (Mail m : mails) {
                        if ( isTrash && m.getTrash()) clean.add(m); // if trash add and continue
                        else if (isSpam && !m.getTrash()) clean.add(m); // if spam check if mail is in trash
                        else if (!m.getSpam() && !m.getTrash() && !isTrash && !isSpam) clean.add(m); // if not spam or trash
                    }

                    Collections.sort(
                            clean,
                            (a, b) -> b.getDate().compareTo(a.getDate()));

                    adapter.submitList(clean);
                });
    }


    /* ------------------------------------------------------------------ */
    /** Opens ComposeMail for drafts or SingleMail for normal mail */
    private void openMail(Mail mail) {
        Class<?> target = mail.getDraft() ? ComposeMail.class : SingleMail.class;
        Intent   intent = new Intent(requireContext(), target);

        if (mail.getDraft()) {
            intent.putExtra(ComposeMail.EXTRA_DRAFT_ID, mail.getId());
        } else {
            intent.putExtra(SingleMail.EXTRA_MAIL_ID, mail.getId());
        }
        startActivity(intent);
    }

    /* ------------------------------------------------------------------ */


    @Override
    public void onSearchQueryChanged(String query) {
        new ViewModelProvider(this)
                .get(MailsViewModel.class)
                .queryMails(query)
                .observe(getViewLifecycleOwner(), results -> {
                    adapter.submitList(results); // no filtering, show all
                });
    }
    /* ------------------------------------------------------------------ */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;   // avoid memory leak
    }

    @Override
    public void onResume() {
        super.onResume();

        viewModel.reload();
    }

}




