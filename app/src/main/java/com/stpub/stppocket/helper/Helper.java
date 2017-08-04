package com.stpub.stppocket.helper;

import android.app.Application;

/**
 * Created by i-worx on 2017-07-14.
 */

public class Helper extends Application {
    private String userId;
    private boolean offline = false; // A flag to indicate whether the user chose browsing offline or not.


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
}
