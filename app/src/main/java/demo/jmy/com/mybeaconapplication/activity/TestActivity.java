package demo.jmy.com.mybeaconapplication.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.skybeacon.sdk.RangingBeaconsListener;
import com.skybeacon.sdk.ScanServiceStateCallback;
import com.skybeacon.sdk.locate.SKYBeacon;
import com.skybeacon.sdk.locate.SKYBeaconManager;
import com.skybeacon.sdk.locate.SKYRegion;

import java.util.ArrayList;
import java.util.List;

import demo.jmy.com.mybeaconapplication.R;
import demo.jmy.com.mybeaconapplication.adapter.MenuAdapter;

public class TestActivity extends AppCompatActivity {
    private static final SKYRegion ALL_SEEKCY_BEACONS_REGION = new SKYRegion("rid_all", null, null, null, null);
    private static final SKYRegion MONITOR_REGION_TEST = new SKYRegion("rid_test", null, "00000000-0000-0000-0000-000000000000", 0, 0);

    private MenuAdapter adapter;
    private List<SKYBeacon> data = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        SKYBeaconManager.getInstance().init(this);
        SKYBeaconManager.getInstance().setCacheTimeMillisecond(5000);//可选，不设置默认为5秒缓存
        SKYBeaconManager.getInstance().setScanTimerIntervalMillisecond(1000);//可选，不设置默认为2秒返回一次数据
        /* 设置SeekcyBeacon防蹭用密钥，若不是防蹭用iBeacon，可以不设置*/
        /*SKYBeaconManager.getInstance().setBroadcastKey("AB11221498756731BCD7D8E239E765AD52B7139DE87654DAB27394BCD7D792A");*/
        SKYBeaconManager.getInstance().setDecryptScan(true);
        SKYBeaconManager.getInstance().setRangingBeaconsListener(new RangingBeaconsListener() {
            @Override
            public void onRangedBeacons(SKYRegion skyRegion, List list) {
                Log.e("test", "onRangedBeacons" + list.size() + " " + skyRegion.getDeviceAddress());
                for (int i = 0; i < list.size(); i++) {
                    SKYBeacon skyRegion1 = (SKYBeacon) list.get(i);
                    addDevice(skyRegion1);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onRangedBeaconsMultiIDs(SKYRegion skyRegion, List list) {
                Log.e("test", "onRangedBeaconsMultiIDs" + list.size() + " " + skyRegion.getDeviceAddress());
            }

            @Override
            public void onRangedNearbyBeacons(SKYRegion skyRegion, List list) {
                Log.e("test", "onRangedNearbyBeacons" + list.size() + " " + skyRegion.getDeviceAddress());
            }
        });

        /*SKYBeaconManager.getInstance().setMonitoringBeaconsListener(new MonitoringBeaconsListener() {
            @Override
            public void onEnteredRegion(SKYRegion skyRegion, List list) {
                Log.e("test", "onEnteredRegion" + list.size() + " " + skyRegion.getDeviceAddress());
            }

            @Override
            public void onExitedRegion(SKYRegion skyRegion, List list) {
                Log.e("test", "onExitedRegion" + list.size() + " " + skyRegion.getDeviceAddress());
            }
        });*/
    }

    private void addDevice(SKYBeacon skyRegion1) {
        for (int i = 0; i < data.size(); i++) {
            if (skyRegion1.getDeviceAddress().equals(data.get(i).getDeviceAddress())) {
                data.set(i, skyRegion1);
                return;
            }
        }
        data.add(skyRegion1);
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv);
        adapter = new MenuAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRanging();
//        startMonitoring();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRanging();
//        stopMonitoring();
    }

    private void startRanging() {
        SKYBeaconManager.getInstance().startScanService(new ScanServiceStateCallback() {
            @Override
            public void onServiceDisconnected() {
            }

            @Override
            public void onServiceConnected() {
                SKYBeaconManager.getInstance().startRangingBeacons(ALL_SEEKCY_BEACONS_REGION);
            }
        });
    }

    private void stopRanging() {
        SKYBeaconManager.getInstance().stopScanService();
        SKYBeaconManager.getInstance().stopRangingBeasons(ALL_SEEKCY_BEACONS_REGION);
    }

    private void startMonitoring() {
        SKYBeaconManager.getInstance().startScanService(new ScanServiceStateCallback() {
            @Override
            public void onServiceDisconnected() {
                // TODO Auto-generated method stub
            }
            @Override
            public void onServiceConnected() {
                // TODO Auto-generated method stub
                SKYBeaconManager.getInstance().startMonitoringBeacons(MONITOR_REGION_TEST);
            }
        });
    }

    private void stopMonitoring() {
        SKYBeaconManager.getInstance().stopScanService();
        SKYBeaconManager.getInstance().stopMonitoringBeacons(MONITOR_REGION_TEST);
    }
}
