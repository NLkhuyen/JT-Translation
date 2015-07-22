package com.example.database;

import java.util.ArrayList;

/**
 * Created by Nghia Pham on 7/19/2015.
 */
public class Word extends Object {
    private int id;
    private String word;
    private ArrayList<WordInfor> wordInfor;
    private int isFavorite;

    public Word(){

    }

    public Word(int id, String word, ArrayList<WordInfor> wordInfor, int isFavorite) {
        this.id = id;
        this.word = word;
        this.wordInfor = wordInfor;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public ArrayList<WordInfor> getWordInfor() {
        return wordInfor;
    }

    public void setWordInfor(ArrayList<WordInfor> wordInfor) {
        this.wordInfor = wordInfor;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}
