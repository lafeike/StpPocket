package com.stpub.stppocket.data;

/**
 * Created by i-worx on 2017-08-17.
 */

public class States {
    private String states;
    private boolean selected;


    public States(String states) {
        this.states = states;
    }

    public String getItems() {
        return states;
    }

    public void setItemName(String name) {
        this.states = name;
    }

    public boolean getSelected() {
        return selected;
    }

    public boolean setSelected(Boolean selected) {
        return this.selected = selected;
    }
}

