package com.stpub.stppocket.helper;

import android.app.Application;

/**
 * Created by i-worx on 2017-07-14.
 */

public class Helper extends Application {
    private String userId;


    public String getUserId(){
        return userId;
    }


    public void setUserId(String userId){
        this.userId = userId;
    }
}
