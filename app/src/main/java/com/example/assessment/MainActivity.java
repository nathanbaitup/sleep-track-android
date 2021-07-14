package com.example.assessment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assessment.entities.Diary;
import com.example.assessment.fragments.DashboardFragment;
import com.example.assessment.fragments.DiaryFragment;
import com.example.assessment.fragments.MonitoringFragment;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String CHANNEL_ID = "channel1";

    private SleepTrackDB sleepTrackDB;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;


    /**
     * The onCreate method that generates the application on startup.
     *
     * @param savedInstanceState - creates the instance.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        // Gets the shared preferences to update the navigation drawer with the user profile picture and name.
        SharedPreferences sharedPreferences = getSharedPreferences("SleepTrack", MODE_PRIVATE);

        // Creates the toolbar, navigation drawer and options menu.
        setupNavigation();
        // Updates the navigation menu to include a profile picture.
        changeNavMenu(sharedPreferences);
        // Sets up the database.
        databaseSetup();
        // Creates a notification.
        notifications();
    }

    /**
     * Tells the app what to do when back is pressed.
     */
    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // REFERENCE accessed 17/05/2021
        // https://stackoverflow.com/a/17369359
        // Used to ensure back button goes through the back stack.
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            //END REFERENCE
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Creates the options menu.
     *
     * @param menu - the menu.
     * @return returns true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * Checks what navigation button has been pressed and redirects to the correct fragment.
     *
     * @param item -the fragment that will be changed.
     * @return returns true.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.app_info) {
            Toast.makeText(this, R.string.app_info_text, Toast.LENGTH_LONG).show();
        } else if (id == R.id.options) {
            Intent intent = new Intent(this, OptionsActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks what navigation button has been pressed and redirects to the correct fragment.
     *
     * @param item -the fragment that will be changed.
     * @return returns true.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.dashboard) {
            // Changes to the dashboard fragment.
            changeFragment(new DashboardFragment());
        } else if (id == R.id.monitoring) {
            // Sets the title bar and changes to the morning monitoring fragment.
            toolbar.setTitle("Morning Monitoring Form:");
            changeFragment(new MonitoringFragment());
        } else if (id == R.id.diary) {
            // Sets the title bar and changes to the diary entry fragment.
            toolbar.setTitle("Diary Entry:");
            changeFragment(new DiaryFragment());
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Public method that is used through a range of fragments to allow a fragment to be switched.
     *
     * @param fragment - the name of the fragment to switch to.
     */
    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Initialises the toolbar, navigation drawer and options menu.
     */
    public void setupNavigation() {
        // Sets the custom toolbar at the top of the application with the app title.
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sleep Track +");
        setSupportActionBar(toolbar);

        // Creates the Navigation Drawer and burger menu.
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_closed) {

            // REFERENCE 04/05/2021
            // https://stackoverflow.com/a/39088728
            // Used to close the keyboard when the app drawer is opened.
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // REFERENCE 16/05/2021
                // https://stackoverflow.com/a/25908854
                // Used to stop application from crashing.
                if (getCurrentFocus() != null)
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                // END REFERENCE
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null)
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            // END REFERENCE
        };
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().performIdentifierAction(R.id.dashboard, 0);
    }

    /**
     * A method dedicated to setting up the database and creating an initial diary entry.
     */
    public void databaseSetup() {

        // Builds the database.
        sleepTrackDB = Room.databaseBuilder(this, SleepTrackDB.class, "SleepTrackDB").build();

        // Creates a means of running database operations in a background thread.
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Runs in the background to check if the initial diary entry has already been entered
        // if not, adds an entry explaining how to use the diary function.
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (sleepTrackDB.diaryDAO().getAllDiaryEntries().size() == 0) {
                    sleepTrackDB.diaryDAO().insertDiary(
                            new Diary("How to Use Diary Feature:", MainActivity.this.getString(R.string.diary_tutorial))
                    );
                }
            }
        });
    }

    /**
     * Updates the navigation drawer whenever the main activity is updated to ensure that the profile picture
     * and username are update once set in the Options Activity.
     *
     * @param sharedPreferences - uses the shared preferences initiated in the onCreate() method.
     */
    public void changeNavMenu(SharedPreferences sharedPreferences) {
        // REFERENCE accessed 02/05/2021
        // https://stackoverflow.com/a/33560593
        // used to get access to the navigation header view to allow the profile picture and username
        // to be updated.
        View navigationHeaderView = navigationView.getHeaderView(0);
        ImageView profilePictureMenu = navigationHeaderView.findViewById(R.id.profile_picture);
        TextView usernameMenu = navigationHeaderView.findViewById(R.id.username_menu);
        // END REFERENCE.

        if (sharedPreferences.getString("ImageTaken", null) != null) {
            // REFERENCE accessed 02/05/2021
            // https://guides.codepath.com/android/Displaying-Images-with-the-Picasso-Library
            // used access profile picture from shared preferences and update the profile picture.
            Picasso.with(this)
                    .load(sharedPreferences.getString("ImageTaken", null))
                    .placeholder(R.mipmap.ic_default_profile_round)
                    .into(profilePictureMenu);
            // END REFERENCE.
        }
        if (sharedPreferences.getString("username", null) != null) {
            usernameMenu.setText(sharedPreferences.getString("username", null));
        }
    }

    /**
     * Sends a notification whenever the app is opened to say it is active.
     */
    public void notifications() {
        // REFERENCE accessed 02/05/2021
        // https://git.cardiff.ac.uk/mobile-development/slide-decks/notifications/-/blob/master/notifications.pdf
        // used to create a notification that works for all required API levels.
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "SleepTrack_Channel", NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("Sleep Track Notification Channel");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Intent appIntent = new Intent(this, MainActivity.class);
        PendingIntent openAppIntent = PendingIntent.getActivity(this, 1, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("SleepTrack +")
                .setContentText("SleepTack + is now active!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .addAction(0, "Open App", openAppIntent)
                .setContentIntent(openAppIntent)
                .setAutoCancel(true);

        Notification notificationCompat = mBuilder.build();
        if (notificationManager != null) {
            notificationManager.notify(0, notificationCompat);
        }
        // END REFERENCE
    }


}


