package com.example.database;

/**
 * Created by Nghia Pham on 7/19/2015.
 */
public class WordInfor {
    private WordInforTitle title;
    private String infor;

    public WordInfor(WordInforTitle title, String infor) {
        this.title = title;
        this.infor = infor;
    }

    public WordInforTitle getTitle() {
        return title;
    }

    public void setTitle(WordInforTitle title) {
        this.title = title;
    }

    public String getInfor() {
        return infor;
    }

    public void setInfor(String infor) {
        this.infor = infor;
    }
}
