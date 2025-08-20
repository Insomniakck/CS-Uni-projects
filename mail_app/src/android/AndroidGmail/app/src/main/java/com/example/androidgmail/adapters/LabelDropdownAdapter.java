package com.example.androidgmail.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidgmail.R;
import com.example.androidgmail.entities.Label;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LabelDropdownAdapter extends ListAdapter<Label, LabelDropdownAdapter.VH> {

    /** IDs that were *already* ticked when the popup opened */
    private final Set<String> originalChecked = new HashSet<>();

    /** IDs that are *currently* ticked after user interaction */
    private final Set<String> currentChecked = new HashSet<>();

    /** the mail we’re editing labels for */
    private final String mailID;

    public LabelDropdownAdapter(String mailID) {
        super(DIFF_CALLBACK);
        this.mailID = mailID;
    }

    /* --- diff util -------------------------------------------------------- */

    private static final DiffUtil.ItemCallback<Label> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<>() {
                @Override public boolean areItemsTheSame(@NonNull Label a, @NonNull Label b) {
                    return a.getId().equals(b.getId());
                }
                @Override public boolean areContentsTheSame(@NonNull Label a, @NonNull Label b) {
                    return a.equals(b);
                }
            };

    /* --- view‑holder ------------------------------------------------------ */

    static class VH extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView  labelName;
        final CheckBox  checkbox;
        VH(View v) {
            super(v);
            icon      = v.findViewById(R.id.iconLabel);
            labelName = v.findViewById(R.id.tvLabelName);
            checkbox  = v.findViewById(R.id.cbLabelSelected);
        }
    }

    /* --- RV callbacks ----------------------------------------------------- */

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.label_item_row, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Label label = getItem(pos);
        String id   = label.getId();

        h.labelName.setText(label.getLabelName());
        h.icon.setImageResource(R.drawable.ic_label);

        /*---------- decide CURRENT checked state ----------*/
        boolean initiallyChecked = label.getMails() != null &&
                label.getMails().contains(mailID);
        boolean checkedNow = currentChecked.contains(id)            // user‑toggled?
                ? true
                : initiallyChecked;

        h.checkbox.setOnCheckedChangeListener(null);
        h.checkbox.setChecked(checkedNow);

        /* remember starting state once */
        if (!originalChecked.contains(id) && initiallyChecked) {
            originalChecked.add(id);
        }
        if (checkedNow) currentChecked.add(id); else currentChecked.remove(id);

        /*---------- listeners ----------*/
        h.checkbox.setOnCheckedChangeListener((b, isChecked) -> {
            if (isChecked) currentChecked.add(id);
            else           currentChecked.remove(id);
        });

        h.itemView.setOnClickListener(v -> h.checkbox.toggle());
    }


    /** Return the list of labels whose checked state changed */
    public List<Label> getChangedLabels() {
        List<Label> changed = new ArrayList<>();
        for (Label l : getCurrentList()) {
            boolean original = originalChecked.contains(l.getId());
            boolean current  = currentChecked.contains(l.getId());
            if (original ^ current) {  // XOR: true if changed
                changed.add(l);
            }
        }
        return changed;

    }
}
