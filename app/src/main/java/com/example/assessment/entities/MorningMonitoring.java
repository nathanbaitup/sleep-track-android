package com.example.assessment.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class MorningMonitoring {

    /**
     * The Primary Key.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * The time of waking up.
     */
    @ColumnInfo(name = "waking_time")
    private String wakingTime;

    /**
     * Waking Heart Rate.
     */
    @ColumnInfo(name = "waking_hr")
    private int wakingHR;

    /**
     * Standing Heart Rate.
     */
    @ColumnInfo(name = "standing_hr")
    private int standingHR;

    /**
     * Mental State.
     */
    @ColumnInfo(name = "mental_state")
    private float mentalState;

    /**
     * Sleep Quality.
     */
    @ColumnInfo(name = "sleep_quality")
    private float sleepQuality;

    /**
     * Sleep Quantity.
     */
    @ColumnInfo(name = "sleep_quantity")
    private int sleepQuantity;

    /**
     * Memorable Dream.
     */
    @ColumnInfo(name = "dreams")
    private String dreams;

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
     * Getter method for the waking time.
     *
     * @return returns the waking time.
     */
    public String getWakingTime() {
        return wakingTime;
    }

    /**
     * Getter method for the waking heart rate.
     *
     * @return returns the waking heart rate.
     */
    public int getWakingHR() {
        return wakingHR;
    }

    /**
     * Getter method for the standing heart rate.
     *
     * @return returns the standing heart rate.
     */
    public int getStandingHR() {
        return standingHR;
    }

    /**
     * Getter method for the mental state.
     *
     * @return returns the mental state.
     */
    public float getMentalState() {
        return mentalState;
    }

    /**
     * Getter method for the sleep quality.
     *
     * @return returns the sleep quality.
     */
    public float getSleepQuality() {
        return sleepQuality;
    }

    /**
     * Getter method for the sleep quantity.
     *
     * @return returns the sleep quantity.
     */
    public int getSleepQuantity() {
        return sleepQuantity;
    }

    /**
     * Getter method for the dreams.
     *
     * @return returns the dreams.
     */
    public String getDreams() {
        return dreams;
    }

    /**
     * Constructor for morning monitoring.
     *
     * @param wakingTime    - the waking time.
     * @param wakingHR      - waking heart rate.
     * @param standingHR    - standing heart rate.
     * @param mentalState   - mental state.
     * @param sleepQuality  = sleep quality.
     * @param sleepQuantity - sleep quantity.
     * @param dreams        - memorable dream.
     */

    public MorningMonitoring(String wakingTime, int wakingHR, int standingHR, float mentalState, float sleepQuality, int sleepQuantity, String dreams) {
        this.wakingTime = wakingTime;
        this.wakingHR = wakingHR;
        this.standingHR = standingHR;
        this.mentalState = mentalState;
        this.sleepQuality = sleepQuality;
        this.sleepQuantity = sleepQuantity;
        this.dreams = dreams;
    }
}
