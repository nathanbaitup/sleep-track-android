package com.example.assessment;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.assessment.dao.DiaryDAO;
import com.example.assessment.dao.MorningMonitoringDAO;
import com.example.assessment.entities.Diary;
import com.example.assessment.entities.MorningMonitoring;

/**
 * The database class where diary and morning monitoring data is saved.
 */
@Database(entities = {Diary.class, MorningMonitoring.class}, version = 1)
public abstract class SleepTrackDB extends RoomDatabase {

    /**
     * @return returns the DiaryDAO.
     */
    public abstract DiaryDAO diaryDAO();

    /**
     * @return returns the MorningMonitoringDAO.
     */
    public abstract MorningMonitoringDAO morningMonitoringDAO();


}
