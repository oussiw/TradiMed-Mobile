package com.example.myapplication;

import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.myapplication.dataTools.Prescription;
import com.example.myapplication.dataTools.PrescriptionViewModel;
import com.example.myapplication.utility.Builder;
import com.example.myapplication.utility.Translator;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Result extends AppCompatActivity {

    private StringBuilder sp,sp2;
    private Uri imageuri;
    private Prescription currentPrescription;
    private PrescriptionViewModel prescriptionViewModel;
    private int new_prescription;
    private Translator translator;
    private EditText edt_result;
    private String edt_result_text_orig;
    private String edt_result_text_trans;
    private EditText edt_title;
    private ImageView imgprev;
    private Button btn_original;
    private Button btn_translate;

    private boolean saveBtn_is_pressed = false;
    private boolean deleteBtn_is_pressed = false;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocaleLanguage();
        setContentView(R.layout.activity_result);

        Toolbar mToolbar;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_name_result);

        buidDict(); // build dictionary before loading result of scan
        loadResutInActivity();
        prescriptionViewModel = ViewModelProviders.of(this).get(PrescriptionViewModel.class);

        btn_original = findViewById(R.id.org_btn);
        btn_original.setEnabled(false);
        Drawable btn_shape_disabled = getResources().getDrawable(R.drawable.btn_shape_disabled);
        btn_original.setBackground(btn_shape_disabled);
        btn_translate = findViewById(R.id.translate_btn);
        btn_translate.setEnabled(true);
//        btn_translate.setBackgroundColor(R.color.colorSecondary);

        btn_original.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Drawable btn_shape_enabled = getResources().getDrawable(R.drawable.btn_shape);
                Drawable btn_shape_disabled = getResources().getDrawable(R.drawable.btn_shape_disabled);
                btn_translate.setEnabled(true);
                btn_translate.setBackground(btn_shape_enabled);
                btn_original.setEnabled(false);
                btn_original.setBackground(btn_shape_disabled);
                toOriginal();
            }
        });

        btn_translate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Drawable btn_shape_enabled = getResources().getDrawable(R.drawable.btn_shape);
                Drawable btn_shape_disabled = getResources().getDrawable(R.drawable.btn_shape_disabled);
                btn_original.setEnabled(true);
                btn_original.setBackground(btn_shape_enabled);
                btn_translate.setEnabled(false);
                btn_translate.setBackground(btn_shape_disabled);
                translateBtn();
            }
        });
    }

    private void loadResutInActivity(){
        new_prescription = getIntent().getIntExtra("new_prescription",0);
        currentPrescription = (Prescription) getIntent().getSerializableExtra("prescription");
        String imageUrl = currentPrescription.getResultUri();
        imageuri = Uri.parse(imageUrl);
        edt_result = findViewById(R.id.edt_result);

        edt_title = findViewById(R.id.edt_title);
        edt_title.setText(currentPrescription.getTitle());

        imgprev = findViewById(R.id.imgpreview);
        imgprev.setImageURI(imageuri);

        BitmapDrawable drawable = (BitmapDrawable) imgprev.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()){
            Toast.makeText(this, R.string.toast_no_text_found, Toast.LENGTH_LONG).show();
            saveBtn_is_pressed = true;
            onBackPressed();
        }
        else{
            if(currentPrescription.getOriginalText() != null && currentPrescription.getTranslatedText() != null){
                edt_result.setText(currentPrescription.getOriginalText());
                edt_result_text_orig = currentPrescription.getOriginalText();
                edt_result_text_trans = currentPrescription.getTranslatedText();
            }
            else{
                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                sp = new StringBuilder();
                sp2 = new StringBuilder();
                for (int i = 0; i<items.size(); i++){
                    TextBlock myItem = items.valueAt(i);
                    sp.append(myItem.getValue());
                    sp.append("\n");
                    String text = myItem.getValue();
                    getText(sp2,text);
                }
                currentPrescription.setOriginalText(sp.toString());
                currentPrescription.setTranslatedText(sp2.toString());
                edt_result_text_orig = currentPrescription.getOriginalText();
                edt_result_text_trans = currentPrescription.getTranslatedText();
                edt_result.setText(currentPrescription.getOriginalText());
                List<String> medocs = translator.getMedicamentos();
                if(medocs.size()==0){
                    Toast.makeText(this, R.string.notPrescDialog_message, Toast.LENGTH_LONG).show();
                    saveBtn_is_pressed = true;
                    onBackPressed();
                }
            }

        }
    }

    private void getText(StringBuilder sp,String text){
        sp.append(translator.getTranslation(text));
        sp.append("\n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_prescription2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // todo: goto back activity from here
                onBackPressed();
                return true;

            case R.id.save2:
                saveBtn();
                return true;

            case R.id.share2:
                shareBtn();
                return true;

            case R.id.delete2:
                deleteBtn();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void saveBtn(){
        String temp_title = edt_title.getText().toString().trim();
        if(temp_title == null || temp_title.equals("")){
            Toast.makeText(getApplicationContext(),R.string.toast_renamed_prescription_failed,Toast.LENGTH_LONG).show();
        }else {
            currentPrescription.setTitle(temp_title);
            if(!btn_original.isEnabled()){
                String text = edt_result.getText().toString();
                currentPrescription.setOriginalText(text);
                currentPrescription.setTranslatedText(translator.getTranslation(text));
            }
            if(new_prescription == 1) prescriptionViewModel.insert(currentPrescription);
            else if(new_prescription == 0) prescriptionViewModel.update(currentPrescription);
            saveBtn_is_pressed = true;
            Toast.makeText(getApplicationContext(),R.string.toast_saved_prescription,Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }

    private void shareBtn() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        edt_result = findViewById(R.id.edt_result);
        sendIntent.putExtra(Intent.EXTRA_TEXT, edt_result.getText());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void deleteBtn(){
        deleteBtn_is_pressed = true;
        onBackPressed();
    }

    public void copybtn(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", sp.toString());
        assert clipboardManager != null;
        clipboardManager.setPrimaryClip(clipData);

        // mInterstitialAd.show();
        Toast.makeText(Result.this, "Copied", Toast.LENGTH_SHORT).show();
    }

    //Functions used by buttons inside scanner activity
    public void toOriginal(){
        edt_result.setText(currentPrescription.getOriginalText());
        String message = getResources().getString(R.string.toast_original_text);
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }

    public void translateBtn(){
        if(currentPrescription.getOriginalText().equals(edt_result.getText().toString())){
            edt_result.setText(currentPrescription.getTranslatedText());
        }
        else{
            String text = edt_result.getText().toString();
            currentPrescription.setOriginalText(text);
            currentPrescription.setTranslatedText(translator.getTranslation(text));
            edt_result.setText(currentPrescription.getTranslatedText());
        }
        String message = getResources().getString(R.string.toast_translated_text);
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }

    private void buidDict(){
        Builder builder = Builder.getInstance();
        translator = new Translator(builder.getDictionaryFr(),builder.getDictionaryAr(),builder.getMedicaments());
    }

    public void restoreText(View view){
        edt_result.setText(edt_result_text_orig);
        Drawable btn_shape_enabled = getResources().getDrawable(R.drawable.btn_shape);
        Drawable btn_shape_disabled = getResources().getDrawable(R.drawable.btn_shape_disabled);
        btn_translate.setEnabled(true);
        btn_translate.setBackground(btn_shape_enabled);
        btn_original.setEnabled(false);
        btn_original.setBackground(btn_shape_disabled);
        currentPrescription.setOriginalText(edt_result_text_orig);
        currentPrescription.setTranslatedText(edt_result_text_trans);
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

    @Override
    public void onBackPressed() {
        if(saveBtn_is_pressed == true) super.onBackPressed();
        else if(deleteBtn_is_pressed == true){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.deleteDialog_title)
                    .setIcon(R.drawable.warning_icon)
                    .setMessage(R.string.deleteDialog_message)
                    .setNegativeButton(R.string.deleteDialog_negative_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteBtn_is_pressed = false;
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(R.string.deleteDialog_positive_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prescriptionViewModel.delete(currentPrescription);
                            Toast.makeText(getApplicationContext(),R.string.toast_deleted_prescription,Toast.LENGTH_SHORT).show();
                            Result.super.onBackPressed();
                        }
                    });
            AlertDialog quitDialog = builder.create();
            quitDialog.show();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.quitDialog_title)
                    .setIcon(R.drawable.warning_icon)
                    .setMessage(R.string.quitDialog_message)
                    .setNegativeButton(R.string.quitDialog_negative_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(R.string.quitDialog_positive_btn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Result.super.onBackPressed();
                        }
                    });
            AlertDialog quitDialog = builder.create();
            quitDialog.show();
        }
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

}
