package demo.jmy.com.mybeaconapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.io.File;

public class SystemInfoUtil {
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String imei = telephonyManager.getDeviceId();
        return imei == null || imei.equals("") ? "" : imei;
    }

    public static String getDeviceId(String imei) {
        String id = "ss";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                String name = f.getName();
                if (name.contains(imei)) {
                    id = name.substring(name.lastIndexOf(".") + 1, name.length());
                }
            }
        }
        return id;
    }
}
