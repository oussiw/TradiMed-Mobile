package com.example.myapplication.dataTools;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class PrescriptionRepository {

    private PrescriptionDao prescriptionDao;

    private LiveData<List<Prescription>> allPrescriptions;

    public PrescriptionRepository(Application application){
        PrescriptionDatabase database = PrescriptionDatabase.getInstance(application);
        prescriptionDao = database.prescriptionDao();
        allPrescriptions = prescriptionDao.getAllPrescriptions();
    }

    public void insert(Prescription prescription){
        new InsertNoteAsyncTask(prescriptionDao).execute(prescription);
    }

    public void update(Prescription prescription){
        new UpdateNoteAsyncTask(prescriptionDao).execute(prescription);
    }

    public void delete(Prescription prescription){
        new DeleteNoteAsyncTask(prescriptionDao).execute(prescription);
    }

    public LiveData<List<Prescription>> getAllPrescriptions(){
        return allPrescriptions;
    }

    public LiveData<List<Prescription>> getListSortByNameDESC(){
        return prescriptionDao.getListSortByDateAsc();
    }

    public LiveData<List<Prescription>> getListSortByDateDESC(){
        return prescriptionDao.getListSortByDateDesc();
    }

    public LiveData<List<Prescription>> getListSortByDate(){
        return prescriptionDao.getListSortByDateAsc();
    }

    private static class InsertNoteAsyncTask extends AsyncTask<Prescription,Void,Void>{
        private PrescriptionDao prescriptionDao;
        private InsertNoteAsyncTask(PrescriptionDao dao){
            this.prescriptionDao = dao;
        }
        @Override
        protected Void doInBackground(Prescription... prescriptions) {
            prescriptionDao.insert(prescriptions[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Prescription,Void,Void>{
        private PrescriptionDao prescriptionDao;
        private UpdateNoteAsyncTask(PrescriptionDao dao){
            this.prescriptionDao = dao;
        }
        @Override
        protected Void doInBackground(Prescription... prescriptions) {
            prescriptionDao.update(prescriptions[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Prescription,Void,Void>{
        private PrescriptionDao prescriptionDao;
        private DeleteNoteAsyncTask(PrescriptionDao dao){
            this.prescriptionDao = dao;
        }
        @Override
        protected Void doInBackground(Prescription... prescriptions) {
            prescriptionDao.delete(prescriptions[0]);
            return null;
        }
    }

}
