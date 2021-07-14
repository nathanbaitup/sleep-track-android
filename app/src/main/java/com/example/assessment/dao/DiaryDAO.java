package com.example.assessment.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.assessment.entities.Diary;

import java.util.List;

@Dao
public interface DiaryDAO {

    /**
     * Gets all diary entries.
     *
     * @return - returns a list of all diary entries.
     */
    @Query("SELECT * FROM Diary")
    List<Diary> getAllDiaryEntries();

    /**
     * Insert a new diary to the database.
     *
     * @param diary - the diary entity.
     */
    @Insert
    void insertDiary(Diary... diary);

    /**
     * Deletes all diary entries.
     */
    @Query("DELETE FROM Diary")
    void deleteAllDiaryEntries();

}
