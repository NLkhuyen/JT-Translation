package com.example.database;

/**
 * Created by Nghia Pham on 7/20/2015.
 */
public class KanjiInfor {
    private KanjiInforTitle title;
    private String infor;

    public KanjiInfor(KanjiInforTitle title, String infor) {
        this.title = title;
        this.infor = infor;
    }

    public KanjiInforTitle getTitle() {
        return title;
    }

    public void setTitle(KanjiInforTitle title) {
        this.title = title;
    }

    public String getInfor() {
        return infor;
    }

    public void setInfor(String infor) {
        this.infor = infor;
    }
}
