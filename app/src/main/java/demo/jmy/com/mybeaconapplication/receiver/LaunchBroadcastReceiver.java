package demo.jmy.com.mybeaconapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import demo.jmy.com.mybeaconapplication.util.MyLogUtil;
import demo.jmy.com.mybeaconapplication.service.MyService;

public class LaunchBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
//        MyLogUtil.clear();
        MyLogUtil.writeCustomLog("start service");
        Log.e("test",intent.getAction());
        context.startService(new Intent(context, MyService.class));
    }
}
