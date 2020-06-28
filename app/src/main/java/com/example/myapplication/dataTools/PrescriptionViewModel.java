package com.example.myapplication.dataTools;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PrescriptionViewModel extends AndroidViewModel {

    private PrescriptionRepository repository;
    private LiveData<List<Prescription>> allPrescriptions;

    public PrescriptionViewModel(@NonNull Application application) {
        super(application);
        repository = new PrescriptionRepository(application);
        allPrescriptions = repository.getAllPrescriptions();
    }

    public void insert(Prescription prescription){
        repository.insert(prescription);
    }

    public void update(Prescription prescription){
        repository.update(prescription);
    }

    public void delete(Prescription prescription){
        repository.delete(prescription);
    }

    public LiveData<List<Prescription>> getAllPrescriptions(){
        return allPrescriptions;
    }

    public LiveData<List<Prescription>> getListSortByNameDESC(){
        return repository.getListSortByNameDESC();
    }

    public LiveData<List<Prescription>> getListSortByDateASC(){
        return repository.getListSortByDate();
    }

    public LiveData<List<Prescription>> getListSortByDateDESC(){
        return repository.getListSortByDateDESC();
    }
}
