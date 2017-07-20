package com.stpub.stppocket.data;

/**
 * Created by i-worx on 2017-07-17.
 */

public class Topic {
    private  String topic;

    public Topic(final String topic){
        this.topic = topic;
    }

    public String getTopic(){
        return topic;
    }

    public void SetTopic(final String topic){
        this.topic = topic;
    }


    @Override
    public String toString(){
        return getTopic();
    }
}
