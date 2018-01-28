package com.vanshil.visionhand;

/**
 * Created by vanshilshah on 2018-01-28.
 */

public class ActionStatus {
    private static ActionStatus instance;
    private String object;
    private double theta;

    Listener listener;


    public static ActionStatus getInstance(){
        if(instance == null){
            instance = new ActionStatus();
        }
        return instance;
    }

    private ActionStatus(){
        this.object = "";
        this.listener = null;
    }

    public synchronized void setTheta(Double theta){
        this.theta = theta;
    }

    public synchronized double getTheta(){
        return theta;
    }

    public synchronized void setObject(String object){
        this.object = object;
        notify(object);
    }

    public synchronized String getObject(){
        return this.object;
    }

    private void notify(String object){
        if (listener != null){
            listener.onObjectAssigned(object);
        }
    }

    interface Listener{
        void onObjectAssigned(String object);
    }
}
