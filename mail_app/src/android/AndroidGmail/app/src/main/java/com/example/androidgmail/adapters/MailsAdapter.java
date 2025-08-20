package com.example.androidgmail.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.androidgmail.R;
import com.example.androidgmail.entities.Mail;

public class MailsAdapter extends ListAdapter<Mail, MailsAdapter.VH> {

    public interface OnMailClick { void onClick(Mail mail); }

    private final OnMailClick listener;

    public MailsAdapter(OnMailClick l) {
        super(new DiffUtil.ItemCallback<Mail>() {
            @Override public boolean areItemsTheSame(@NonNull Mail a, @NonNull Mail b) {
                return a.getId().equals(b.getId());
            }
            @Override public boolean areContentsTheSame(@NonNull Mail a, @NonNull Mail b) {
                return a.equals(b);
            }
        });
        this.listener = l;
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView sender, subject, snippet, date;
        VH(View v) {
            super(v);
            avatar  = v.findViewById(R.id.imgAvatar);
            sender  = v.findViewById(R.id.txtSender);
            subject = v.findViewById(R.id.txtSubject);
            snippet = v.findViewById(R.id.txtSnippet);
            date    = v.findViewById(R.id.txtDate);
        }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vType){
        View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_mail,p,false);
        return new VH(v);
    }


    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Mail m = getItem(pos);
        String imageUrl = m.getSenderProfileImage();

        if (imageUrl != null && !imageUrl.isEmpty()) {

            Glide.with(h.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .circleCrop()
                    .into(h.avatar);
        } else {
            Glide.with(h.itemView.getContext())
                    .load(R.drawable.default_profile)
                    .circleCrop()
                    .into(h.avatar);
        }

        h.sender.setText(m.getSender());
        h.subject.setText(m.getSubject());
        h.snippet.setText(m.getContent());
        h.date.setText(m.getParsedDate());
        h.itemView.setOnClickListener(v -> listener.onClick(m));
    }
}
