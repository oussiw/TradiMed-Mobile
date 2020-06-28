package com.example.myapplication.utility;

import java.util.ArrayList;
import java.util.List;

public class Translator {

    private List<String[]> dictionaryFr;
    private List<String[]> dictionaryAr;
    private List<String[]> medicaments;
    private List<String> medicamentos;

    public List<String> getMedicamentos() {
        return medicamentos;
    }

    public Translator(List<String[]> dictionaryFr, List<String[]> dictionaryAr, List<String[]> medicaments) {
        this.dictionaryFr = dictionaryFr;
        this.dictionaryAr = dictionaryAr;
        this.medicaments = medicaments;
        medicamentos = new ArrayList<>();
    }

    public String getTranslation(String sr){
        String result= new String() ;
        String entry = removeSuspeciousCharac(sr);
        String[] elementsParent = entry.trim().split("\n");
        boolean first_medic = false;
        int index = 0;
        for(String elementParent:elementsParent){
            List<CouplePuctuation> cps = new ArrayList<>();
            List<CoupleTranslated> cts = new ArrayList<>();
            String[] elements = elementParent.replaceAll("\\s+"," ").split(" ");
            //cette boucle sert a parcourir la liste splitee par des " "
            for(int i=0;i<elements.length;i++){
                index++;
                String str="";
                String tmp ="";
                String strO = "";
                String strT = "";
                CouplePuctuation cp = null;
                //cette boucle sert a concatener des elements et chercher une correspondance prealable
                for(int j=i;j<elements.length;j++){
                    str += elements[j];
                    //parcourir les medicaments
//                    cp = locatePunctuation(elementParent);
                    String str2 = removePunctuation(str);
                    String svg;
                    if(!(svg = getMedocTranslation(str2,first_medic)).equals("")){
                        first_medic = true;
                        tmp = svg;
                        strO = str2;
                        strT = svg;
                        medicamentos.add(str2);
                        cp = locatePunctuation(str,index);
                        i=j;
                    }
                    else if(!(svg = getTransByWord(str2.toLowerCase())).equals("")) {
                        tmp = svg;
                        strO = str2;
                        strT = svg;
                        cp = locatePunctuation(str,index);
                        i=j;
                    }
                    else if(!(svg = getTransByKeyWord(str2.toLowerCase())).equals("")) {
                        tmp = svg;
                        strO = str2;
                        strT = svg;
                        cp = locatePunctuation(str,index);
                        i=j;
                    }
                    str = str +' ';
                }

                //here we add words for which we didn't find translation
                if(tmp.equals("")) {
                    cts.add(new CoupleTranslated(elements[i],elements[i],index));
                    if(cp!=null) cps.add(cp);
//                    result = result + ' ' + elements[i]+' ';
                }
                //we append the result stored previously in the tmp variable
                else {
                    cts.add(new CoupleTranslated(strO,strT,index));
                    if(cp!=null) cps.add(cp);
//                    result += tmp+' ';
                }
            }
            cps.size();
            cts.size();
            result += restorePunctuation(cps,cts);
            result += "\n";
        }
        return result;
    }

    private String getMedocTranslation(String str, boolean first_medic){
        String tmp ="";
        for(int k=0; k < medicaments.size();k++){
            if(medicaments.get(k)[0].toLowerCase().equals(str.toLowerCase())){
                if(!first_medic){
                    tmp = "\n" +  medicaments.get(k)[2];

                }else {
                    tmp = "\n\n" + medicaments.get(k)[2];
                }
                break;
            }
        }
        return tmp;
    }

    private CouplePuctuation locatePunctuation(String entry,int index){
        String[] punctuations = {",",";",":","."};
        CouplePuctuation couple = null;
        for(String c:punctuations){
            if(entry.contains(c)){
                String v = entry.replace(c,"");
                couple = new CouplePuctuation(v,c,index);
                break;
            }
        }
        return couple;
    }

    private String removePunctuation(String entry){
        String[] punctuations = {",",";",":","."};
        String str = entry;
        for(String c:punctuations){
            if(str.contains(c)) str = str.replace(c,"");
        }
        return str;
    }

    private String restorePunctuation(List<CouplePuctuation> couplePuctuations,List<CoupleTranslated> coupleTranslateds){
        String arabicStr = "";
        boolean found ;
        for(CoupleTranslated ct:coupleTranslateds){
            found = false;
            for (CouplePuctuation cp:couplePuctuations){
                if(ct.index==cp.index){
                    arabicStr += ct.translated.concat(cp.punctuation) + " ";
                    found = true;
                }
            }
            if(!found) arabicStr += ct.translated + " ";
        }
        return arabicStr;
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

    private String findKey(String entry_word){
        String entry = entry_word.toLowerCase();
        if(entry.startsWith("a")){
            for(int k=18;k <= 29;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("b")){
            for(int k=30;k <= 31;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("c")){
            for(int k=32;k <= 45;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("d")){
            for(int k=46;k <= 60;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("e")){
            for(int k=61;k <= 65;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("f")){
            for(int k = 66;k <= 67;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("g")){
            for(int k=68;k <= 74;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("h")){
            for(int k=75;k <= 77;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("i")){
            for(int k=78;k <= 88;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("j")){
            for(int k=0;k <dictionaryFr.size();k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("k")){
            for(int k=89;k <= 89;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("l")){
            for(int k=90;k <= 91;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("m")){
            for(int k=92;k <= 96;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("n")){
            for(int k=97;k <= 100;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("o")){
            for(int k=101;k <= 107;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("p")){
            for(int k=108;k <= 130;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("q")){
            for(int k=131;k <= 139;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("r")){
            for(int k=140;k <= 143;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("s")){
            for(int k=144;k <= 158;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("t")){
            for(int k=159;k <= 169;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("u")){
            for(int k=170;k <= 170;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("v")){
            for(int k=171;k <= 174;k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("w")){
            for(int k=0;k < dictionaryFr.size();k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("x")){
            for(int k=0;k < dictionaryFr.size();k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("y")){
            for(int k=0;k < dictionaryFr.size();k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else if(entry.startsWith("z")){
            for(int k=0;k < dictionaryFr.size();k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        else{
            for(int k=0;k < dictionaryFr.size();k++){
                for(int j=0;j<dictionaryFr.get(k).length;j++){
                    if(entry.equals(dictionaryFr.get(k)[j].toLowerCase())) return dictionaryFr.get(k)[0].toLowerCase();
                }
            }
        }
        return "";
    }

    // entry should be in lowercase
    private String getTransByWord(String entry_word){
        String entry = entry_word.toLowerCase();
        if(entry.startsWith("a")){
            for(int k=95;k <= 132;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase())){
                    return dictionaryAr.get(k)[1];
                }

            }
        }
        else if(entry.startsWith("b")){
            for(int k=133;k <= 139;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("c")){
            for(int k=140;k <= 164;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("d")){
            for(int k=165;k <= 193;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("e")){
            for(int k=194;k <= 208;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("f")){
            for(int k = 209;k <= 216;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("g")){
            for(int k=217;k <= 232;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("h")){
            for(int k=233;k <= 238;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("i")){
            for(int k=239;k <= 273;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("j")){
            for(int k=274;k <= 278;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("k")){
            for(int k=279;k <= 279;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("l")){
            for(int k=280;k <= 289;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("m")){
            for(int k=290;k <= 302;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("n")){
            for(int k=303;k <= 308;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("o")){
            for(int k=309;k <= 320;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("p")){
            for(int k=321;k <= 370;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("q")){
            for(int k=371;k <= 379;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("r")){
            for(int k=381;k <= 390;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("s")){
            for(int k=391;k <= 431;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("t")){
            for(int k=432;k <= 450;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("u")){
            for(int k=451;k <= 465;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("v")){
            for(int k=466;k <= 471;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("w")){
            for(int k=0;k < dictionaryAr.size();k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("x")){
            for(int k=472;k <= 476;k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("y")){
            for(int k=0;k < dictionaryAr.size();k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else if(entry.startsWith("z")){
            for(int k=0;k < dictionaryAr.size();k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        else{
            for(int k=0;k < dictionaryAr.size();k++){
                if(entry.equals(dictionaryAr.get(k)[0].toLowerCase()))
                    return dictionaryAr.get(k)[1];
            }
        }
        return "";
    }

    private String getTransByKeyWord(String entry_word){
        return getTransByWord(findKey(entry_word));
    }

    private class CoupleTranslated{
        String original;
        String translated;
        int index;

        public CoupleTranslated(String original, String translated, int index) {
            this.original = original;
            this.translated = translated;
            this.index = index;
        }

        public CoupleTranslated(String original) {
            this.original = original;
        }

        public CoupleTranslated(String original, String translated) {
            this.original = original;
            this.translated = translated;
        }
    }

    private class CouplePuctuation{
        String str;
        String punctuation;
        int index;

        public CouplePuctuation(String str, String punctuation, int index) {
            this.str = str;
            this.punctuation = punctuation;
            this.index = index;
        }

        public CouplePuctuation() {
        }
    }
}
