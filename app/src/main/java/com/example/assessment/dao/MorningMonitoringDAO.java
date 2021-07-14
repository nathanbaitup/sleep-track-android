package com.example.assessment.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.assessment.entities.MorningMonitoring;

import java.util.List;

@Dao
public interface MorningMonitoringDAO {

    /**
     * Gets a list of morning monitoring data.
     *
     * @return - returns a list of morning monitoring data.
     */
    @Query("SELECT * FROM MorningMonitoring")
    List<MorningMonitoring> getAllMonitoring();

    /**
     * Inserts morning monitoring data.
     *
     * @param morningMonitoring - the morning monitoring entity.
     */
    @Insert
    void insertMorningMonitoring(MorningMonitoring... morningMonitoring);

    /**
     * Deletes all data from MorningMonitoring.
     */
    @Query("DELETE FROM MorningMonitoring")
    void deleteAllMorningMonitoringEntries();
}
