package edu.cuhk.csci3310.cuwalk.sportRecord;

import android.util.Log;

public class Timer extends Thread{
    private long startTime;
    private long lastedTime;
    @Override
    public void run(){
        startTime = System.currentTimeMillis();
        while(true){
            lastedTime = System.currentTimeMillis() - startTime;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Log.i("appInfo",""+lastedTime);
        }
    }
}
