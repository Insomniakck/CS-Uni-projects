package com.example.androidgmail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.androidgmail.adapters.LabelsAdapter;
import com.example.androidgmail.dao.LocalDatabase;
import com.example.androidgmail.databinding.ActivityMainBinding;
import com.example.androidgmail.entities.Label;
import com.example.androidgmail.helpers.LabelsHelper;
import com.example.androidgmail.repositories.SessionManager;
import com.example.androidgmail.viewmodels.LabelViewModel;
import com.example.androidgmail.viewmodels.UserViewModel;
import com.google.android.material.navigation.NavigationView;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private AppBarConfiguration appBarConfig;
    private NavController navController;
    private LabelViewModel labelVM;

    UserViewModel userVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        binding.appBarMain.fab.setOnClickListener(v ->
                startActivity(new Intent(this, ComposeMail.class)));

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView   navView       = findViewById(R.id.nav_view);
        RecyclerView     labelsRecycler = findViewById(R.id.labels_recycler);

        // Setup NavController and app bar with static default menu folders
        appBarConfig = new AppBarConfiguration.Builder(
                R.id.nav_inbox, R.id.nav_custom_label)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            getSupportActionBar().setTitle("");
        });

        NavigationUI.setupWithNavController(navView, navController);

        // Handle the  "+" menu item in the drawer menu to add new labels
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            String label = null;

            if (id == R.id.nav_inbox)       label = "inbox";
            else if (id == R.id.nav_sent)   label = "sent";
            else if (id == R.id.nav_drafts) label = "draft";
            else if (id == R.id.nav_starred) label = "starred";
            else if (id == R.id.nav_trash)  label = "trash";
            else if (id == R.id.nav_spam)   label = "spam";
            else if (id == R.id.nav_add_label) {
                LabelsHelper.showAddLabelPopup(this, findViewById(R.id.drawer_layout), labelVM);
                return false; // don’t close drawer
            }

            if (label != null) {
                // Navigate to label fragment with argument
                Bundle args = new Bundle();
                args.putString("label", label);
                navController.navigate(R.id.nav_inbox, args);
                binding.drawerLayout.close();
                return true;
            }

            // Fallback: Try default navigation behavior
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) binding.drawerLayout.close();
            return handled;
        });


        labelVM = new ViewModelProvider(this).get(LabelViewModel.class);
        userVM = new ViewModelProvider(this).get(UserViewModel.class);


        // Setup RecyclerView inside the drawer for showing dynamic labels
        LabelsAdapter labelsAdapter = new LabelsAdapter(new LabelsAdapter.OnLabelClick() {
            @Override
            public void onClick(Label label) {

                Bundle args = new Bundle();
                args.putString("labelId", label.getId());


                navController.navigate(R.id.nav_custom_label, args);

                binding.drawerLayout.close();
            }


            @Override
            public void onDelete(Label label) {
                labelVM.deleteLabel(label.getId())
                        .observe(MainActivity.this,
                                ok -> {
                                    if (Boolean.TRUE.equals(ok)) {
                                        labelVM.reload();   // reload only after delete finishes
                                    }
                                });
            }

            @Override
            public void onEdit(Label label) {
                LabelsHelper.showEditLabelPopup(MainActivity.this, binding.getRoot(), label, labelVM);
            }
        });

        labelsRecycler.setAdapter(labelsAdapter);
        labelsRecycler.setLayoutManager(new LinearLayoutManager(this));
        labelsRecycler.setNestedScrollingEnabled(false);

        labelVM.getCustomLabels().observe(this, labelsAdapter::submitList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        View    searchViewContainer = searchItem.getActionView();
        SearchView sv = searchViewContainer.findViewById(R.id.custom_search_view);

        // Set dynamic max width based on screen width
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidthPx = metrics.widthPixels;

// e.g., use ~60% of screen width
        int maxWidthPx = (int) (screenWidthPx * 0.7f);
        sv.setMaxWidth(maxWidthPx);

        sv.setIconifiedByDefault(false);
        sv.findViewById(androidx.appcompat.R.id.search_plate)
                .setBackgroundResource(R.drawable.search_background);
        View mag = sv.findViewById(androidx.appcompat.R.id.search_mag_icon);
        mag.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        EditText et = sv.findViewById(androidx.appcompat.R.id.search_src_text);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.GRAY);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) { forwardQuery(q); return true; }
            @Override
            public boolean onQueryTextChange(String q) { forwardQuery(q); return true; }
            private void forwardQuery(String q) {
                Fragment f = getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_content_main)
                        .getChildFragmentManager()
                        .getPrimaryNavigationFragment();
                if (f instanceof SearchableFragment) {
                    ((SearchableFragment) f).onSearchQueryChanged(q);
                }
            }
        });


        MenuItem userItem = menu.findItem(R.id.action_user);
        View     userView = userItem.getActionView();
        userView.setOnClickListener(v -> showUserCard());

        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(
                navController, appBarConfig)
                || super.onSupportNavigateUp();
    }

    @SuppressLint("SetTextI18n")
    private void showUserCard() {
        View view = getLayoutInflater().inflate(R.layout.user_card, null);

        int sizeInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

        final PopupWindow popup = new PopupWindow(view,
                sizeInPx,
                sizeInPx,
                true);

        TextView usernameText = view.findViewById(R.id.username);
        ImageView profileImageView = view.findViewById(R.id.profile_image);

        userVM.getLoggedInUser().observe(this, user -> {
            if (user == null) {
                return;
            }
            usernameText.setText("Hi, " + user.getFirstName() + "!");

            // Check and load image
            if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                String fullImageUrl =  user.getProfileImageUrl();

                Glide.with(this)
                        .load(fullImageUrl)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .circleCrop()
                        .into(profileImageView);
            } else {
                // Use default image
                profileImageView.setImageResource(R.drawable.default_profile);
            }
        });


        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            popup.dismiss();

            SessionManager.clear(MainActivity.this);
            Executors.newSingleThreadExecutor().execute(() -> {
                LocalDatabase.getInstance(MainActivity.this).clearAllTables();
            });

            Intent intent = new Intent(MainActivity.this, LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        popup.setElevation(10f);
        popup.showAtLocation(findViewById(R.id.toolbar), Gravity.END | Gravity.TOP, 40, 180);
    }

}
