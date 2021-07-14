package com.example.assessment.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Diary {

    /**
     * The Primary Key.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * The diary title.
     */
    @ColumnInfo(name = "diary_title")
    private String title;

    /**
     * The diary entry.
     */
    @ColumnInfo(name = "diary_entry")
    private String entry;

    /**
     * Getter method to get the id.
     *
     * @return returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter method to set the id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter method to get the title.
     *
     * @return returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter method to get the entry.
     *
     * @return returns the entry.
     */
    public String getEntry() {
        return entry;
    }

    /**
     * Constructor method for the Diary.
     *
     * @param title - the title of the diary.
     * @param entry - the diary entry.
     */
    public Diary(String title, String entry) {
        this.title = title;
        this.entry = entry;
    }
}
