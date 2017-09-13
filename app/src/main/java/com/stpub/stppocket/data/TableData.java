package com.stpub.stppocket.data;

/**
 * Created by i-worx on 2017-07-18.
 */

public class TableData {

    private  String title;
    private String key;
    private String parentKey;
    private int pid;

    public TableData(final String title, final String key){
        this.title = title;
        this.key = key;
    }

    public String getTitle(){
        return title;
    }


    public String getKey(){
        return key;
    }

    public void setPid(final int pid) { this.pid = pid;};

    public int getPid(){return pid;}


    public void setTitle(final String title){
        this.title = title;
    }


    public void setKey(final String key) {
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
