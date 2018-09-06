package demo.jmy.com.mybeaconapplication.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

public class LaunchService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    if(!isServiceRunning("MyService")){
                        startService(new Intent(LaunchService.this,MyService.class));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public boolean isServiceRunning(String className){
        boolean isRunning = false;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(30);
        if(list.size()==0){
            return isRunning;
        }
        for(int i=0;i<list.size();i++){
            if(list.get(i).service.getClassName().equals(className)){
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
