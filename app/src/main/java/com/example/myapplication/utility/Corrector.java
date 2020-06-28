package com.example.myapplication.utility;

import java.util.List;

public class Corrector {

    private List<String[]> dictionaryFr;
    private List<String[]> dictionaryAr;
    private List<String[]> medicaments;

    public Corrector(List<String[]> dictionaryFr, List<String[]> dictionaryAr, List<String[]> medicaments) {
        this.dictionaryFr = dictionaryFr;
        this.dictionaryAr = dictionaryAr;
        this.medicaments = medicaments;
    }

    public String getVerifiedText(String sr){
        String [] punctuations = {",",";",":","."};
        String result= new String() ;
        String entry = removeSuspeciousCharac(sr);
        String[] elementsParent = entry.trim().split("\n");
        boolean first_medic = false;
        for(String elementParent:elementsParent){
            String[] elements = elementParent.replaceAll("\\s+"," ").split(" ");
            //cette boucle sert a parcourir la liste splitee par des " "
            for(int i=0;i<elements.length;i++){
                String str="";
                String tmp ="";
                String punctation = "";
                boolean containPunctuation = false;
                //cette boucle sert a concatener des elements et chercher une correspondance prealable
                for(int j=i;j<elements.length;j++){
                    str = str + elements[j];
                    containPunctuation = false;
                    punctation = "";
                    String str2 = str;
                    for(String c:punctuations){
                        if(str.endsWith(c)){
                            punctation = c;
                            str2 = str.replace(c,"");
                            containPunctuation = true;
                            break;
                        }
                        else containPunctuation = false;
                    }
                    //parcourir les medicaments
                    for(int k=0; k < medicaments.size();k++){
                        if(medicaments.get(k)[0].toLowerCase().equals(str2.toLowerCase())){
                            if(!first_medic){
                                tmp = medicaments.get(k)[2];
                                first_medic = true;
                            }else {
                                tmp = "\n\n" + medicaments.get(k)[2];
                            }
                            i=j;
                            break;
                        }
                    }
                    String svg;

                    if(containPunctuation == true) break;
                    str = str +' ';
                }
                if(!punctation.equals("") && containPunctuation == true) tmp = tmp + punctation;

                //here we add words for which we didn't find translation
                if(tmp.equals("")) {
                    result = result + ' ' + elements[i]+' ';
                }
                //we append the result stored previously in the tmp variable
                else {
                    result = result + tmp+' ';
                }
            }
            result = result + "\n";
        }
        return result;
    }
    private String removeSuspeciousCharac(String entry){
        String tmp = entry.toLowerCase();
        String[] characA = {"à","á","â","ä","ã","å"};
        String[] characE = {"é","è","ê","ë"};
        String[] characC = {"ç"};
        String[] characY = {"ý","ÿ"};
        String[] characU = {"ù","ú","û","ü"};
        String[] characO = {"ð","ò","ó","ô","õ","ö"};
        String[] characI = {"ì","í","î","ï"};
        for(String c:characA){
            tmp = tmp.replaceAll(c,"a");
        }
        for(String c:characE){
            tmp = tmp.replaceAll(c,"e");
        }
        for(String c:characC){
            tmp = tmp.replaceAll(c,"c");
        }
        for(String c:characY){
            tmp = tmp.replaceAll(c,"y");
        }
        for(String c:characU){
            tmp = tmp.replaceAll(c,"u");
        }
        for(String c:characO){
            tmp = tmp.replaceAll(c,"o");
        }
        for(String c:characI){
            tmp = tmp.replaceAll(c,"i");
        }
        return tmp;
    }

}
