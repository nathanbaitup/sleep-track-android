package com.example.assessment.utilities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.assessment.MainActivity;

import static android.content.Context.MODE_PRIVATE;

//REFERENCE ACCESSED 04/05/2021
// https://developer.android.com/guide/topics/ui/dialogs
// used to get access to dialog boxes.

public class DeleteSPDialogue extends DialogFragment {


    /**
     * Creates a DialogFragment.
     *
     * @param savedInstanceState - the savedInstanceState.
     * @return returns a built dialog asking to delete all shared preferences.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Gets the shared preferences to update the navigation drawer with the user profile picture and name.
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("SleepTrack", MODE_PRIVATE);
        Editor editor = sharedPreferences.edit();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete your Username and Profile Picture?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("username", null);
                        editor.putString("ImageTaken", null);
                        editor.apply();

                        Intent refreshIntent = new Intent(DeleteSPDialogue.this.getActivity(), MainActivity.class);
                        DeleteSPDialogue.this.startActivity(refreshIntent);
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
