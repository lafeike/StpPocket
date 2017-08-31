package com.stpub.stppocket.helper;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i-worx on 2017-07-14.
 */

public class Helper extends Application {
    private String userId;
    private boolean offline = false; // A flag to indicate whether the user chose browsing offline or not.
    private ArrayList<String> stateList = new ArrayList<String>();
    private Integer stateSelected = 0;


    public String getUserId(){
        return userId;
    }


    public void setUserId(String userId){
        this.userId = userId;
    }


    public boolean getOffline(){
        return offline;
    }


    public void setOffline(boolean offline){
        this.offline = offline;
    }

    public void setStates(ArrayList<String> states){
        this.stateList = states;
    }


    public ArrayList<String> getStates(){
        return stateList;
    }


    public void setStateSelected(Integer i){
        stateSelected = i;
    }


    public Integer getStateSelected(){
        return stateSelected;
    }

    public Integer getStatesCount(){
        return stateList.size();
    }
}
