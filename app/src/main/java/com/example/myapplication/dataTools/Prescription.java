package com.example.myapplication.dataTools;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "prescriptions_table")
public class Prescription implements Serializable {

    @PrimaryKey(autoGenerate = true )
    private int id;

    private String title;

    private String originalText;

    private String translatedText;

    private String dateOfCreation;

    private String resultUri;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getResultUri() {
        return resultUri;
    }

    public void setResultUri(String resultUri) {
        this.resultUri = resultUri;
    }

    public Prescription(){}

    public Prescription(String title, String dateOfCreation) {
        this.title = title;
        this.dateOfCreation = dateOfCreation;
    }

    public Prescription(String title, String dateOfCreation, String resulturi) {
        this.title = title;
        this.resultUri = resulturi;
        this.dateOfCreation = dateOfCreation;
    }

    public Prescription(String title, String originalText, String translatedText, String dateOfCreation, String resulturi) {
        this.title = title;
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.dateOfCreation = dateOfCreation;
        this.resultUri = resulturi;
    }

}
