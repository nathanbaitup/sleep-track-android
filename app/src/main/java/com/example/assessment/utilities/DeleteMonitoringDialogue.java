package com.example.assessment.utilities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;

import com.example.assessment.MainActivity;
import com.example.assessment.SleepTrackDB;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//REFERENCE ACCESSED 04/05/2021
// https://developer.android.com/guide/topics/ui/dialogs
// used to get access to dialog boxes.

public class DeleteMonitoringDialogue extends DialogFragment {

    private SleepTrackDB sleepTrackDB;
    private ExecutorService executorService;

    /**
     * Creates a DialogFragment.
     *
     * @param savedInstanceState - the savedInstanceState.
     * @return returns a built dialog asking to delete all morning monitoring data.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Builds the database.
        sleepTrackDB = Room.databaseBuilder(getActivity(), SleepTrackDB.class, "SleepTrackDB").build();

        // Creates a means of running database operations in a background thread.
        executorService = Executors.newSingleThreadExecutor();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete all Morning Monitoring Entries?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                sleepTrackDB.morningMonitoringDAO().deleteAllMorningMonitoringEntries();
                            }
                        });
                        Intent refreshIntent = new Intent(DeleteMonitoringDialogue.this.getActivity(), MainActivity.class);
                        DeleteMonitoringDialogue.this.startActivity(refreshIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
