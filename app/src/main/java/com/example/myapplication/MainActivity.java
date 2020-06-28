package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.dataTools.Prescription;
import com.example.myapplication.dataTools.PrescriptionViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.google.android.material.navigation.NavigationView;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener,Serializable, EditDialog.EditDialogListener, LanguageDialog.LanguageDialogListener {

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    private String[] cameraPermission;
    private String[] storagePermission;
    private  Uri imageuri;

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private MyAdapter adapter;

    private PrescriptionViewModel prescriptionViewModel;
    public List<Prescription> allPrescriptions;
    private Prescription selected_prescription;

    private FloatingActionButton fab_add,fab_camera,fab_gallery;
    private TextView tv_camera,tv_gallery;
    private Animation fab_anim_open,fab_anim_close;

    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocaleLanguage();
        setContentView(R.layout.activity_main);

        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //Toolbar and hamburger menu
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// fab settings
        fab_add = findViewById(R.id.fab_add);
        fab_camera = findViewById(R.id.fab_camera);
        fab_gallery = findViewById(R.id.fab_gallery);
        tv_camera = findViewById(R.id.tv_camera);
        tv_gallery = findViewById(R.id.tv_gallery);
        fab_anim_open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_anim_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        isOpen = false;
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFab();
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //RecyclerView
        recyclerView = findViewById(R.id.recyclerView_prescriptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        adapter = new MyAdapter(allPrescriptions);
        prescriptionViewModel = ViewModelProviders.of(this).get(PrescriptionViewModel.class);
        prescriptionViewModel.getListSortByDateDESC().observe(this, new Observer<List<Prescription>>() {
            @Override
            public void onChanged(List<Prescription> prescriptions) {
                adapter.setPrescriptions(prescriptions);
                allPrescriptions = prescriptions;
                allPrescriptions.size();
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.setMyContext(this.getApplicationContext());
        adapter.setActivity(this);
        adapter.setFragmentManager(this.getSupportFragmentManager());
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Prescription prescription) {
                if (isOpen) setFabInvisible();
                Intent intent = new Intent(getApplicationContext(), Result.class);
                intent.putExtra("prescription",prescription);
                intent.putExtra("new_prescription",0);
                startActivity(intent);
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Spinner
        String[] sort_options = getResources().getStringArray(R.array.spinner_items);
        Spinner spinner = findViewById(R.id.spinner_sort);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(this.getApplicationContext(),android.R.layout.simple_spinner_item,sort_options);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void loadImagefromGallery(View view) {
        showImageImportDialog();
    }

    private void showImageImportDialog() {
        //Items to display in dialog menu
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getApplicationContext());
        builder.setTitle("Select Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //Camera option clicked
                    if(!checkcamerapermission()){
                        //camera permission not allowed, request it
                        requestcamerapermission();
                    }
                    else{
                        pickcamera();
                    }
                }
                if(which==1){
                    //Camera option clicked
                    if(!checkstoragepermission()){
                        //gallery permission not allowed, request it
                        requeststoragepermission();
                    }
                    else{
                        pickgallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    public void openGallery(View view){
        if (isOpen) setFabInvisible();
        pickgallery();
    }
    private void pickgallery() {
        if(!checkstoragepermission()){
            requeststoragepermission();
        }
        else{
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
        }
    }

    public void openCamera(View view){
        if (isOpen) setFabInvisible();
        pickcamera();
    }
    private void pickcamera() {
        if(!checkcamerapermission()){
            requestcamerapermission();
        }
        else{
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Pic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Images to Text");
            imageuri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
            startActivityForResult(cameraintent, IMAGE_PICK_CAMERA_CODE);
        }
    }

    private void requeststoragepermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkstoragepermission() {
        boolean result = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestcamerapermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkcamerapermission() {
        boolean result = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    //handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean camerAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    if(camerAccepted && writeStorageAccepted){
                        pickcamera();
                    }
                    else{
                        Toast.makeText(this.getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[0]==
                            PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickgallery();
                    }
                    else{
                        Toast.makeText(this.getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //handle Image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                if (data != null) {
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(this);
                }
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imageuri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resulturi = result.getUri();
                String temptitle = getResources().getString(R.string.temp_prescription_title);
                Prescription prescription = new Prescription(temptitle,getLocaleDate(),resulturi.toString());
                Intent intent = new Intent(getApplicationContext(), Result.class);
                intent.putExtra("prescription",prescription);
                intent.putExtra("new_prescription",1);
                startActivity(intent);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this.getApplicationContext(), "" + error, Toast.LENGTH_SHORT).show();

            }
        }
    }

    //Hamburger menu selected item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.ic_settings:
                if (isOpen) setFabInvisible();
                SharedPreferences sharedPreferences = getSharedPreferences("settings",MODE_PRIVATE);
                String language = sharedPreferences.getString("language_chosen","fr");
                int choosed_lang = 0;
                if(language.equals("fr")) choosed_lang = 0;
                else if(language.equals("ar")) choosed_lang = 1;
                LanguageDialog languageDialog = new LanguageDialog(choosed_lang);
                languageDialog.show(getSupportFragmentManager(),"Language Dialog");
                break;

            case R.id.ic_info:
                if (isOpen) setFabInvisible();
                startActivity(new Intent(getApplicationContext(),About.class));
                break;

            case R.id.ic_credits:
                if (isOpen) setFabInvisible();
                startActivity(new Intent(getApplicationContext(),Credits.class));
                break;

            case R.id.ic_contact:
                if (isOpen) setFabInvisible();
                startActivity(new Intent(getApplicationContext(),Contact.class));
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true ;
    }

    //Item context menu
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case 121:
                selected_prescription = allPrescriptions.get(item.getGroupId());
                adapter.renameBtn(selected_prescription.getTitle());
                return true;
            case 122:
                adapter.shareBtn(allPrescriptions.get(item.getGroupId()).getOriginalText()+"\n\n"+allPrescriptions.get(item.getGroupId()).getTranslatedText());
                return true;
            case 123:
                adapter.deleteBtn(item.getGroupId());
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onConfirmClicked(String new_title) {
        new_title = new_title.trim();
        if(new_title == null || new_title.equals("")){
            String t = getResources().getString(R.string.toast_renamed_prescription_failed);
            Toast.makeText(getApplicationContext(),t,Toast.LENGTH_LONG).show();
        }
        else{
            if(adapter.getSelectedPrescription() != null) selected_prescription = adapter.getSelectedPrescription();
            selected_prescription.setTitle(new_title);
            adapter.updatePrescription(selected_prescription);
            String t = getResources().getString(R.string.toast_renamed_prescription);
            Toast.makeText(getApplicationContext(),t,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position)
        {
            case 0:
                prescriptionViewModel.getAllPrescriptions().observe(this, new Observer<List<Prescription>>() {
                    @Override
                    public void onChanged(List<Prescription> prescriptions) {
                        adapter.setPrescriptions(prescriptions);
                        adapter.notifyDataSetChanged();
                        allPrescriptions = prescriptions;
                        allPrescriptions.size();
                    }
                });
                break;

            case 1:
                prescriptionViewModel.getListSortByDateDESC().observe(this, new Observer<List<Prescription>>() {
                    @Override
                    public void onChanged(List<Prescription> prescriptions) {
                        adapter.setPrescriptions(prescriptions);
                        adapter.notifyDataSetChanged();
                        allPrescriptions = prescriptions;
                        allPrescriptions.size();
                    }
                });
                break;

            case 2:
                prescriptionViewModel.getListSortByDateASC().observe(this, new Observer<List<Prescription>>() {
                    @Override
                    public void onChanged(List<Prescription> prescriptions) {
                        adapter.setPrescriptions(prescriptions);
                        adapter.notifyDataSetChanged();
                        allPrescriptions = prescriptions;
                        allPrescriptions.size();
                    }
                });
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String getLocaleDate(){
        Locale locale = new Locale("fr","FR");
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT,locale);
        String date = dateFormat.format(new Date());
        return date;
    }

    private void manageFab(){

        if(isOpen){
            setFabInvisible();
        }
        else {
            setFabVisible();
        }
    }

    private void setFabVisible(){
        OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(fab_add).
                rotation(135).
                withLayer().
                setDuration(400).
                setInterpolator(interpolator).
                start();
        fab_camera.setAnimation(fab_anim_open);
        ViewCompat.animate(fab_camera).
                rotation(0).
                withLayer().
                setDuration(300).
                setInterpolator(interpolator).
                start();
        fab_gallery.setAnimation(fab_anim_open);
        ViewCompat.animate(fab_gallery).
                rotation(0).
                withLayer().
                setDuration(300).
                setInterpolator(interpolator).
                start();
        tv_camera.setVisibility(View.VISIBLE);
        tv_gallery.setVisibility(View.VISIBLE);
        isOpen = true;
    }

    private void setFabInvisible(){
        OvershootInterpolator interpolator = new OvershootInterpolator();
        ViewCompat.animate(fab_add).
                rotation(0).
                withLayer().
                setDuration(400).
                setInterpolator(interpolator).
                start();
        fab_camera.setAnimation(fab_anim_close);
        ViewCompat.animate(fab_camera).
                rotation(90).
                withLayer().
                setDuration(1000).
                setInterpolator(interpolator).
                start();
        fab_gallery.setAnimation(fab_anim_close);
        ViewCompat.animate(fab_gallery).
                rotation(90).
                withLayer().
                setDuration(1000).
                setInterpolator(interpolator).
                start();
        tv_camera.setVisibility(View.INVISIBLE);
        tv_gallery.setVisibility(View.INVISIBLE);
        isOpen = false;
    }

    @Override
    public void onSelectedLanguage(String[] languages, int choosed_language) {
        if(choosed_language == 0){
            setLocale("fr");
            recreate();
        }
        else if(choosed_language == 1) {
            setLocale("ar");
            recreate();
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
