package com.example.assessment.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.HandlerCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.assessment.utilities.DiaryAdapter;
import com.example.assessment.MainActivity;
import com.example.assessment.R;
import com.example.assessment.SleepTrackDB;
import com.example.assessment.entities.Diary;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiaryFragment extends Fragment {

    private ListView lv;
    private SleepTrackDB sleepTrackDB;
    private Handler handler;

    /**
     * Creates the Diary Fragment View.
     *
     * @param inflater - the layout inflater.
     * @param container - the container.
     * @param savedInstanceState - the saved instance state.
     * @return returns the inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        // Builds the database.
        sleepTrackDB = Room.databaseBuilder(getActivity(), SleepTrackDB.class, "SleepTrackDB").build();

        // Creates a means of running database operations in a background thread.
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Create an object that enables code to be run on the main UI thread.
        handler = HandlerCompat.createAsync(Looper.getMainLooper());

        // Gets the list view.
        lv = view.findViewById(R.id.example_list_view);

        // OnClickListener to view a specific diary entry.
        viewAnEntry();

        // Runs in a background thread to retrieve all of the current diary entries.
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                // Creates an array list to store all diary entries for use with the diaryAdapter.
                ArrayList<Diary> diaryEntries = new ArrayList<>();
                List<Diary> allEntries = sleepTrackDB.diaryDAO().getAllDiaryEntries();
                for (int i = 0; i < allEntries.size(); i++) {
                    diaryEntries.add(new Diary(allEntries.get(i).getTitle(), allEntries.get(i).getEntry()));
                }

                // Connects to the diary adapter to display all the diary entries in a list format.
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                            DiaryAdapter diaryAdapter = new DiaryAdapter(DiaryFragment.this.getContext(), R.layout.diary_list_view, diaryEntries);
                            lv.setAdapter(diaryAdapter);
                    }
                });
            }
        });

        // Changes the fragment to the diary entry fragment.
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) DiaryFragment.this.getActivity();
                mainActivity.changeFragment(new DiaryEntryFragment());
            }
        });
        return view;
    }

    /**
     * Sets the dialog to the entry that was selected.
     *
     * @return returns an ArrayList with the entry.
     */
    public ArrayList<Diary> viewAnEntry() {
        ArrayList<Diary> diaries = new ArrayList<>();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!DiaryFragment.this.viewAnEntry().isEmpty()) {
                    DiaryFragment.this.viewAnEntry().clear();
                }

                // Splits the entry into its title and entry to save to the ArrayList.
                String[] returnSplit = lv.getItemAtPosition(position).toString().split("=");
                String[] titleSplit = returnSplit[2].split(", entry");
                String entrySplit = returnSplit[3];

                diaries.add(new Diary(titleSplit[0], entrySplit));
                DiaryFragment.this.addCustomDialog(diaries);
            }
        });
        return diaries;
    }

    /**
     * Builds a custom alert dialog to display an entry.
     *
     * @param diaries - the diary ArrayList.
     */
    private void addCustomDialog(ArrayList<Diary> diaries) {
        // REFERENCE accessed 16/05/2021
        // https://ghostcode.in/2016/11/05/how-to-create-a-custom-alert-dialog-in-android-about-feedback-dialog/
        //Used to make a custom alert dialog to display the diary entry.
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View diaryView = inflater.inflate(R.layout.fragment_diary_dialogue, null);

        AlertDialog builder = new AlertDialog.Builder(getContext())
                .setTitle("Diary Entry:")
                .setView(diaryView)
                .setCancelable(true)
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
        // END REFERENCE

        TextView diaryTitle = builder.findViewById(R.id.dialogue_diary_title);
        diaryTitle.setText(diaries.get(0).getTitle());
        TextView diaryEntry = builder.findViewById(R.id.dialogue_diary_entry);
        diaryEntry.setText(diaries.get(0).getEntry());

    }
}
