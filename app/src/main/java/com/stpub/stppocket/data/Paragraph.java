package com.stpub.stppocket.data;

import android.util.Log;

/**
 * Created by i-worx on 2017-07-24.
 */

public class Paragraph extends TableData {
    private int key;
    private int sectionKey;
    private String paraNum;
    private String question;
    private String guideNote;
    private String title;

    public Paragraph(final String title, final int key){
        super(title, key);
    }

    public int getParaKey() {
        return key;
    }

    public int getSectionKey(){
        return sectionKey;
    }

    public void setSectionKey(int sectionKey){
        this.sectionKey = sectionKey;
    }


    public String getParaNum(){
        return paraNum;
    }


    public void setParaNum(String paraNum){
        this.paraNum = paraNum;
    }


    public String getQuestion(){
        return question;
    }


    public void setQuestion(String question){
        this.question = question;
    }


    public String getGuideNote(){
        return guideNote;
    }


    public void setGuideNote(String guideNote){
        this.guideNote = guideNote;
    }


}
