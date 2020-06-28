package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.myapplication.utility.Builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocaleLanguage();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Thread background = new Thread()
        {
            public void run(){
                try{
                    sleep(2*1000);
                    Builder builder = Builder.getInstance();
                    builder.setDictionaryAr(getArDictionary());
                    builder.setDictionaryFr(getFrDictionary());
                    builder.setMedicaments(getMedicaments());
                    Intent intent = new Intent(Splash.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }catch (Exception exc){
                }
            }
        };
        background.start();
    }

    private void setLocale (String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        //Save to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("settings",MODE_PRIVATE).edit();
        editor.putString("language_chosen",lang);
        editor.apply();
    }

    private void loadLocaleLanguage(){
        SharedPreferences sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language = sharedPreferences.getString("language_chosen","fr");
        setLocale(language);
    }

    private List<String[]> getArDictionary(){
        String line ="";
        String csvSplitBy = ",";

        List<String[]> tableau = new ArrayList<>();

        InputStream inputStream = getResources().openRawResource(R.raw.arabicdictionary);
        BufferedReader br= new BufferedReader(new InputStreamReader(inputStream , StandardCharsets.UTF_8));
        try {
            while ((line=br.readLine())!=null){
                String[] translation = line.split(csvSplitBy);
                tableau.add(translation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br!=null){
                try{
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableau;
    }

    private List<String[]> getFrDictionary(){
        String line ="";
        String csvSplitBy = ",";

        List<String[]> tableau = new ArrayList<>();

        InputStream inputStream = getResources().openRawResource(R.raw.frenchdictionary);
        BufferedReader br= new BufferedReader(new InputStreamReader(inputStream , StandardCharsets.UTF_8));
        try {
            while ((line=br.readLine())!=null){
                String[] translation = line.split(csvSplitBy);
                tableau.add(translation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br!=null){
                try{
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableau;
    }

    private List<String[]> getMedicaments(){
        String line ="";
        String csvSplitBy = " -- ";

        List<String[]> tableau = new ArrayList<>();

        InputStream inputStream = getResources().openRawResource(R.raw.list_medicaments_marocains);
        BufferedReader br= new BufferedReader(new InputStreamReader(inputStream , StandardCharsets.UTF_8));
        try {
            while ((line=br.readLine())!=null){
                String[] translation = line.split(csvSplitBy);
                tableau.add(translation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(br!=null){
                try{
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableau;
    }

}
