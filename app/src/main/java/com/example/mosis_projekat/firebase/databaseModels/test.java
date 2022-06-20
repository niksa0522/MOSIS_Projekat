package com.example.mosis_projekat.firebase.databaseModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {
    private String Name;
    public List<String> getKeywords(){
        String[] words = Name.split("\\s+");
        List<String> capitalWords = new ArrayList<>();
        for(String s : words){
            String[] cw = s.split("(?=\\p{Upper})");
            for(int i=0;i<cw.length;i++){
                cw[i] = cw[i].toLowerCase();
            }
            capitalWords.addAll(Arrays.asList(cw));
        }
        for(int i=1;i<Name.length();i++){
            String str = Name.substring(0,i);
            str=str.toLowerCase();
            capitalWords.add(str);
        }
        capitalWords.add(Name.toLowerCase());
        return capitalWords;
    }
}
