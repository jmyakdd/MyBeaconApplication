package demo.jmy.com.mybeaconapplication;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyLogUtil {

    private static final String LOG_NAME = getCurrentDateString() + ".txt";
    private static final String CUSTOM_LOG_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mybeacon/log/";
    private static final int dayPre = 0;
    /**
     * 获取当前日期
     *
     * @return
     */
    private static String getCurrentDateString() {
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm",
                Locale.getDefault());
        Date nowDate = new Date();
        result = sdf.format(nowDate);
        return result;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    private static String getCurrentTimeString() {
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());
        Date nowDate = new Date();
        result = sdf.format(nowDate);
        return result;
    }

    /**
     * 自定义打印日志到本地
     *
     * @param s
     */
    public static void writeCustomLog(String s) {
        File dir = new File(CUSTOM_LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, LOG_NAME);
        s+=getCurrentTimeString()+":";
        s += "\n";
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(s.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        File dir = new File(CUSTOM_LOG_DIR);
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        long timePre = getDayPreTime();
        try {
            for (File f : files) {
                if (timePre > f.lastModified()) {
                    f.deleteOnExit();
                }
            }
        }catch (Exception e){
            MyLogUtil.writeCustomLog(e.toString());
        }
    }

    public static long getDayPreTime(){
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE,-dayPre);
        Date date1 = calendar.getTime();
        return date1.getTime();
    }
}
