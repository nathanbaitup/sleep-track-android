package com.example.assessment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import androidx.fragment.app.Fragment;

import androidx.room.Room;

import com.example.assessment.MainActivity;
import com.example.assessment.R;
import com.example.assessment.SleepTrackDB;

import com.example.assessment.entities.Diary;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiaryEntryFragment extends Fragment {

    private ExecutorService executorService;
    private SleepTrackDB sleepTrackDB;

    AppCompatEditText title;
    AppCompatEditText entry;
    AppCompatButton submit;

    /**
     * Creates the Diary Entry Fragment View.
     *
     * @param inflater - the layout inflater.
     * @param container - the container.
     * @param savedInstanceState - the saved instance state.
     * @return returns the inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary_entry, container, false);

        // Builds the database.
        sleepTrackDB = Room.databaseBuilder(getActivity(), SleepTrackDB.class, "SleepTrackDB").build();
        // The diary title.
        title = view.findViewById(R.id.diary_title);
        // The diary entry.
        entry = view.findViewById(R.id.diary_entry);
        // The submit button.
        submit = view.findViewById(R.id.submit_button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checks if all fields have values.
                DiaryEntryFragment.this.setDefaultEntries();

                // Creates a means of running database operations in a background thread.
                executorService = Executors.newSingleThreadExecutor();

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Checks if text entries are not null and saves to database.
                        if (title.getText() != null && entry.getText() != null)
                            sleepTrackDB.diaryDAO().insertDiary(
                                    new Diary(title.getText().toString(), entry.getText().toString()));
                    }
                });
                // Changes back to the diary fragment.
                DiaryEntryFragment.this.returnToDiaryFragment();
            }
        });
        return view;
    }

    /**
     * Checks if a field has been left blank and inputs a default detail.
     */
    private void setDefaultEntries() {
        if (title.getText().toString() == null || title.getText().toString().equals("")) {
            title.setText(R.string.untitled);
        }
        if (entry.getText().toString() == null || entry.getText().toString().equals("")) {
            entry.setText(R.string.untitled);
        }
    }

    /**
     * Returns to the Diary fragment after submitting a new diary entry.
     */
    private void returnToDiaryFragment() {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.changeFragment(new DiaryFragment());

        // Closes the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        Toast toast = Toast.makeText(getContext(), "Entry has been saved", Toast.LENGTH_SHORT);
        toast.show();
    }

}
