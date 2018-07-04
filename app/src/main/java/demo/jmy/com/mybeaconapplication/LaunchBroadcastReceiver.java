package demo.jmy.com.mybeaconapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LaunchBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        MyLogUtil.clear();
        MyLogUtil.writeCustomLog("start service");
        Log.e("test",intent.getAction());
        context.startService(new Intent(context, MyService.class));
    }
}
