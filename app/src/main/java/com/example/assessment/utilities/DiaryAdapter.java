package com.example.assessment.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.appcompat.widget.AppCompatTextView;

import com.example.assessment.R;
import com.example.assessment.entities.Diary;

import java.util.ArrayList;

// REFERENCE ACCESSED 29/04/2021
// https://git.cardiff.ac.uk/mobile-development/sample-projects/live-sessions/lists-session/-/blob/master/app/src/main/java/com/example/livesessionlists/MyCustomAdapter.java
// Used live session code to create a list adapter.
public class DiaryAdapter extends BaseAdapter {

    private final Context context;
    private final int layoutToUseForTheRows;
    private final ArrayList<Diary> data;

    /**
     * Creates the diary adapter for use with the fragment.
     *
     * @param context - the application context.
     * @param layout - the list view layout file.
     * @param data - the data to go into the file.
     */
    public DiaryAdapter(Context context, int layout, ArrayList<Diary> data) {
        this.context = context;
        this.data = data;
        this.layoutToUseForTheRows = layout;
    }

    /**
     * Gets the total size of the data.
     *
     * @return returns the size of the data.
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * Gets data at the current position.
     *
     * @param position - the position to find data.
     * @return returns data at a position.
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * Gets the ID at the current position.
     *
     * @param position - the position to find the ID.
     * @return returns the ID at a position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Gets the view and sets the diary data on the lists.
     *
     * @param position - the current position.
     * @param convertView - the view to convert to.
     * @param parent - the parent ViewGroup.
     * @return returns the converted view with all items displayed in a list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //creates the user interface for a particular row.
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(this.layoutToUseForTheRows, parent, false);
        }
        Diary diary = (Diary) this.getItem(position);

        AppCompatTextView topText = convertView.findViewById(R.id.title);
        topText.setText(diary.getTitle());
        AppCompatTextView bottomText = convertView.findViewById(R.id.entry);
        bottomText.setText(diary.getEntry());

        return convertView;
    }
}
