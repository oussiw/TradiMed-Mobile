package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Contact extends AppCompatActivity {

    private TextView tv_email1;
    private TextView tv_email2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocaleLanguage();
        setContentView(R.layout.activity_contact);
        getSupportActionBar().setTitle(R.string.activity_name_contacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_email1 = findViewById(R.id.tv_email1);
        tv_email2 = findViewById(R.id.tv_email2);
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

    public void copybtn1(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Email", tv_email1.getText().toString());
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(clipData);
        String message = getResources().getString(R.string.toast_email_copied);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void copybtn2(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Email", tv_email2.getText().toString());
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(clipData);
        String message = getResources().getString(R.string.toast_email_copied);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
