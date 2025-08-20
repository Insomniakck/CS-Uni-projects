package com.example.androidgmail.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidgmail.R;
import com.example.androidgmail.entities.Label;

public class LabelsAdapter extends ListAdapter<Label, LabelsAdapter.VH> {

    public interface OnLabelClick {
        void onClick (Label label);  // single‑tap
        void onDelete(Label label);  // choose “Delete”
        void onEdit  (Label label);  // choose “Edit”
    }

    private final OnLabelClick listener;

    public LabelsAdapter(OnLabelClick l) {
        super(new DiffUtil.ItemCallback<Label>() {
            @Override public boolean areItemsTheSame (@NonNull Label a, @NonNull Label b){ return a.getId().equals(b.getId()); }
            @Override public boolean areContentsTheSame(@NonNull Label a, @NonNull Label b){ return a.equals(b); }
        });
        this.listener = l;
    }

    /* ------------------------------------------------------------------ */
    static class VH extends RecyclerView.ViewHolder {
        TextView labelName;
        View     menuBtn;
        VH(View v){
            super(v);
            labelName = v.findViewById(R.id.label_name);
            menuBtn   = v.findViewById(R.id.label_menu_button);
        }
    }

    /* ------------------------------------------------------------------ */
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int vt){
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.nav_label_item, p, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos){
        Label label = getItem(pos);
        h.labelName.setText(label.getLabelName());

        /* single‑tap opens / selects */
        h.itemView.setOnClickListener(v -> listener.onClick(label));

        /* long‑press anywhere in the row shows the popup */
        h.itemView.setOnLongClickListener(v -> {
            showPopup(v, label);
            return true;
        });

        /* 3‑dot button shows the same popup */
        h.menuBtn.setOnClickListener(v -> showPopup(v, label));
    }

    /* ------------------------------------------------------------------ */
    /** Re‑usable helper to inflate & handle the Edit / Delete menu. */
    private void showPopup(View anchor, Label label){
        PopupMenu pm = new PopupMenu(anchor.getContext(), anchor);
        // show menu xml
        pm.inflate(R.menu.label_item_menu);
        pm.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_edit) {
                Toast.makeText(anchor.getContext(),
                        "Edit " + label.getLabelName(), Toast.LENGTH_SHORT).show();
                listener.onEdit(label);
                return true;

            } else if (id == R.id.menu_delete) {
                Toast.makeText(anchor.getContext(),
                        "Deleted " + label.getLabelName(), Toast.LENGTH_SHORT).show();
                listener.onDelete(label);
                return true;
            }

            return false;
        });

        pm.show();
    }
}
