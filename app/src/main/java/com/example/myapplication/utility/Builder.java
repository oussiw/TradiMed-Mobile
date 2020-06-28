package com.example.myapplication.utility;

import java.util.List;

public class Builder{

    private List<String[]> dictionaryFr;
    private List<String[]> dictionaryAr;
    private List<String[]> medicaments;

    public List<String[]> getDictionaryFr() {
        return dictionaryFr;
    }

    public List<String[]> getDictionaryAr() {
        return dictionaryAr;
    }

    public List<String[]> getMedicaments() {
        return medicaments;
    }

    private static Builder instance ;

    private Builder(){
    }

    public static Builder getInstance(){
        if(instance == null){
            instance = new Builder();
            System.out.println("\n\nNew builder\n\n");
        }else System.out.println("\n\nOld builder\n\n");
        return instance;
    }

    public void setDictionaryFr(List<String[]> dictionaryFr) {
        this.dictionaryFr = dictionaryFr;
    }

    public void setDictionaryAr(List<String[]> dictionaryAr) {
        this.dictionaryAr = dictionaryAr;
    }

    public void setMedicaments(List<String[]> medicaments) {
        this.medicaments = medicaments;
    }

}
