package com.example.database;

import java.util.ArrayList;

/**
 * Created by Nghia Pham on 7/20/2015.
 */
public class Kanji extends Object {
    private int id;
    private String word1;
    private String word2;
    private ArrayList<KanjiInfor> content;
    private int isFavorite;

    public Kanji() {
    }

    public Kanji(int id, String word1, String word2, ArrayList<KanjiInfor> content, int isFavorite) {
        this.id = id;
        this.word1 = word1;
        this.word2 = word2;
        this.content = content;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord1() {
        return word1;
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public ArrayList<KanjiInfor> getContent() {
        return content;
    }

    public void setContent(ArrayList<KanjiInfor> content) {
        this.content = content;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}
