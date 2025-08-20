package com.example.androidgmail.helpers;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.lifecycle.LifecycleOwner;
import com.example.androidgmail.R;
import com.example.androidgmail.api.UserAPI;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.repositories.SessionManager;
import com.example.androidgmail.viewmodels.LabelViewModel;
import java.util.ArrayList;

/**
 * Utility class for managing label-related popup windows (add/edit).
 */
public class LabelsHelper {

    public static void showAddLabelPopup(Activity activity, View rootView, LabelViewModel labelViewModel) {
        View popupView = activity.getLayoutInflater().inflate(R.layout.popup_add_label, null);
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int popupWidth = (int) (displayMetrics.widthPixels * 0.7);


        // Create a popup window with inflated view
        final PopupWindow popupWindow = new PopupWindow(popupView,
                popupWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Get UI references inside the popup
        EditText labelInput = popupView.findViewById(R.id.edit_label_name);
        Button createButton = popupView.findViewById(R.id.btn_create_label);

        // Get the currently logged-in user's ID
        String userID = SessionManager.getUserId(activity);
        UserAPI userAPI = new UserAPI(activity);

        // Get user details (mainly to retrieve the username)
        userAPI.getUserByID(userID).observe((LifecycleOwner) activity, user -> {
            if (user != null) {
                String username = user.getUsername();

                // Set up the "Create" button click handler
                createButton.setOnClickListener(v -> {
                    String labelName = labelInput.getText().toString().trim();

                    if (!labelName.isEmpty()) {
                        Label label = new Label();
                        label.setLabelName(labelName);
                        label.setUsername(username);
                        label.setMails(new ArrayList<>());

                        labelViewModel.addLabel(label).observe((LifecycleOwner) activity, id -> {
                            if (id != null && !id.isEmpty()) {
                                labelViewModel.reload(); // Refresh label list
                                popupWindow.dismiss();   // Close popup on success
                            } else {
                                Toast.makeText(activity, "Failed to add label", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        labelInput.setError("Label name required");
                    }
                });

            } else {
                Toast.makeText(activity, "Failed to load user data", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        // Style and show the popup
        popupWindow.setElevation(10f);
        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
    }

    public static void showEditLabelPopup(Activity activity, View anchorView, Label label, LabelViewModel labelViewModel) {
        View popupView = activity.getLayoutInflater().inflate(R.layout.popup_edit_label, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Get references to UI components inside popup
        EditText labelInput = popupView.findViewById(R.id.edit_label_name);
        Button updateButton = popupView.findViewById(R.id.btn_update_label);

        labelInput.setText(label.getLabelName());

        // Handle "Update" button click
        updateButton.setOnClickListener(v -> {
            String newLabelName = labelInput.getText().toString().trim();

            if (newLabelName.isEmpty()) {
                labelInput.setError("Label cannot be empty");
            } else {
                label.setLabelName(newLabelName);
                labelViewModel.editLabel(label.getId(), label)
                        .observe((LifecycleOwner) activity, ok -> {
                            if (Boolean.TRUE.equals(ok)) {
                                labelViewModel.reload();
                                Toast.makeText(activity, "Label updated", Toast.LENGTH_SHORT).show();
                                popupWindow.dismiss();
                            } else {
                                Toast.makeText(activity, "Failed to update label", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Show the popup
        popupWindow.setElevation(10f);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

}
