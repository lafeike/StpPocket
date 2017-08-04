package com.stpub.stppocket.data;

/**
 * Created by i-worx on 2017-07-18.
 */

public class TableData {

    private  String title;
    private int key;
    private String parentKey;

    public TableData(final String title, final int key){
        this.title = title;
        this.key = key;
    }

    public String getTitle(){
        return title;
    }


    public int getKey(){
        return key;
    }

    public void setTitle(final String title){
        this.title = title;
    }


    public void setKey(final int key) {
        this.key = key;
    }


    public String getParentKey(){
        return parentKey;
    }


    public void setParentKey(String parentKey){
        this.parentKey = parentKey;
    }


    @Override
    public String toString(){
        return getTitle();
    }
}
