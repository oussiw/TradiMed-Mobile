package com.example.myapplication.dataTools;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PrescriptionDao {

    @Insert
    void insert(Prescription prescription);

    @Update
    void update(Prescription prescription);

    @Delete
    void delete(Prescription prescription);

    @Query("SELECT * FROM prescriptions_table ORDER BY title")
    LiveData<List<Prescription>> getAllPrescriptions();

    @Query("SELECT * FROM prescriptions_table ORDER BY title DESC")
    LiveData<List<Prescription>> getListSortByNameDESC();

    @Query("SELECT * FROM prescriptions_table ORDER BY dateOfCreation DESC")
    LiveData<List<Prescription>> getListSortByDateDesc();

    @Query("SELECT * FROM prescriptions_table ORDER BY dateOfCreation")
    LiveData<List<Prescription>> getListSortByDateAsc();
}
