package com.example.androidgmail.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidgmail.R;
import com.example.androidgmail.adapters.LabelDropdownAdapter;
import com.example.androidgmail.entities.Label;

import java.util.List;

public class LabelPickerPopup extends PopupWindow {

    /** Fires once for each *changed* label when OK is pressed */
    public interface OnPick { void accept(@NonNull Label label); }

    public LabelPickerPopup(Context ctx,
                            LifecycleOwner owner,
                            LiveData<List<Label>> labelsLive,
                            OnPick onPick,
                            String mailID) {

        super(LayoutInflater.from(ctx).inflate(R.layout.popup_label_picker, null),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        setElevation(12f);

        /* ---------- RecyclerView -------------------- */
        RecyclerView rv = getContentView().findViewById(R.id.rvLabels);
        rv.setLayoutManager(new LinearLayoutManager(ctx));
        rv.setOverScrollMode(View.OVER_SCROLL_NEVER);

        LabelDropdownAdapter adapter = new LabelDropdownAdapter(mailID);
        rv.setAdapter(adapter);
        labelsLive.observe(owner, adapter::submitList);

        TextView tvEmpty = getContentView().findViewById(R.id.tvEmpty);

        labelsLive.observe(owner, list -> {
            adapter.submitList(list);

            boolean empty = list == null || list.isEmpty();
            rv.setVisibility(empty ? View.GONE : View.VISIBLE);
            tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        });

        /* ---------- OK / Cancel --------------------- */
        Button btnOk     = getContentView().findViewById(R.id.btnOk);
        Button btnCancel = getContentView().findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dismiss());

        btnOk.setOnClickListener(v -> {
            for (Label changed : adapter.getChangedLabels()) {
                onPick.accept(changed);
            }
            dismiss();
        });
    }

    /** show anchored just under the given view */
    public void show(View anchor) { showAsDropDown(anchor, 0, 8); }
}
