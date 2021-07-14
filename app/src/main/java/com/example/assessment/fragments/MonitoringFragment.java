package com.example.assessment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TimePicker;
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
import com.example.assessment.entities.MorningMonitoring;
import com.google.android.material.slider.Slider;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonitoringFragment extends Fragment {

    private SleepTrackDB sleepTrackDB;
    private ExecutorService executorService;
    String timeString;

    /**
     * Creates the Monitoring Fragment View.
     *
     * @param inflater - the layout inflater.
     * @param container - the container.
     * @param savedInstanceState - the saved instance state.
     * @return returns the inflated view.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoring, container, false);

        // The time picker - sets the time to 24 hour view and saves to a string to save to the database.
        TimePicker timePicker = view.findViewById(R.id.waking_time_picker);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view1, int hourOfDay, int minute) {
                timeString = hourOfDay + ":" + minute;
            }
        });
        // Waking heart rate.
        final AppCompatEditText wakingHR = view.findViewById(R.id.waking_hr);
        // Standing heart rate.
        final AppCompatEditText standingHR = view.findViewById(R.id.standing_hr);
        // Mental state slider.
        final Slider mentalState = view.findViewById(R.id.mental_state);
        // Sleep quality slider.
        final Slider sleepQuality = view.findViewById(R.id.sleep_quality);
        // Sleep quantity.
        final AppCompatEditText sleepQuantity = view.findViewById(R.id.sleep_quantity);
        // A memorable dream from the night.
        final AppCompatEditText memorableDream = view.findViewById(R.id.memorable_dream);
        // The submit button.
        AppCompatButton submitButton = view.findViewById(R.id.monitoring_button);

        // Builds the database.
        sleepTrackDB = Room.databaseBuilder(getActivity(), SleepTrackDB.class, "SleepTrackDB").build();

        // Creates a means of running database operations in a background thread.
        executorService = Executors.newSingleThreadExecutor();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        // Inserts all morning monitoring data into the database.
                        try {
                            sleepTrackDB.morningMonitoringDAO().insertMorningMonitoring(
                                    new MorningMonitoring(
                                            timeString,
                                            Integer.parseInt(wakingHR.getText().toString()),
                                            Integer.parseInt(standingHR.getText().toString()),
                                            mentalState.getValue(), sleepQuality.getValue(),
                                            Integer.parseInt(sleepQuantity.getText().toString()),
                                            memorableDream.getText().toString()));
                        } catch (Exception e) {
                            //Logs any error found that stops data from being saved into the database.
                            Log.d("DB_Error", "Unable to save to save to database");
                        }
                    }
                });
                MonitoringFragment.this.returnToDashboard();
            }
        });
        return view;
    }

    private void returnToDashboard() {
        // Changes the fragment back to the dashboard.
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.changeFragment(new DashboardFragment());

        //Closes the keyboard when leaving the fragment.
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        //Shows the user the data has been saved.
        Toast.makeText(getActivity(), "Monitoring data has been saved.", Toast.LENGTH_SHORT).show();
    }
}
