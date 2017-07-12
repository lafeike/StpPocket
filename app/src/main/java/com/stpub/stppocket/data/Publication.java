package com.stpub.stppocket.data;

/**
 * Created by i-worx on 2017-07-10.
 */

public class Publication {
    private  String acronym;
    private String title;

    public Publication(final String acronym, final String title){
        this.acronym = acronym;
        this.title = title;
    }

    public String getAcronym(){
        return acronym;
    }

    public void SetAcronym(final String acronym){
        this.acronym = acronym;
    }

    public String getTitle(){
        return title;
    }

    public void SetTitle(final  String title){
        this.title = title;
    }

    @Override
    public String toString(){
        return getAcronym();
    }
}
