package edu.cuhk.csci3310.cuwalk.sportRecord;

import android.util.Log;
/**
 * @author williw23 DAIWeican
 */
public class Timer{
    private long startTime;
    private long Duration;
    private boolean timerEnabled = false;

    private Thread t;
    public Timer() {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                startTime = System.currentTimeMillis();
                while (timerEnabled == true) {
                    Duration = System.currentTimeMillis() - startTime;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                    Log.i("appInfo", "" + Duration);
                }
            }
        });
    }

    public void startTimer(){
        timerEnabled = true;
        t.start();
    }

    public long getStartTime(){
        return startTime;
    }
    public long getCurrentTime(){return System.currentTimeMillis();}
    public Long getDuration(){
        return Duration;
    }
}
