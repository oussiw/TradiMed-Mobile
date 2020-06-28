package com.example.myapplication.dataTools;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Prescription.class} , version = 1)
public abstract class PrescriptionDatabase extends RoomDatabase {

    private static PrescriptionDatabase instance;

    public abstract PrescriptionDao prescriptionDao();

    public static synchronized PrescriptionDatabase getInstance(Context context){
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    PrescriptionDatabase.class,"prescription_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>{

        private PrescriptionDao dao;

        private PopulateDbAsyncTask(PrescriptionDatabase db){
            dao = db.prescriptionDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
