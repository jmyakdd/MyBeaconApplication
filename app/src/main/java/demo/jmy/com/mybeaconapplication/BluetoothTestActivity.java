package demo.jmy.com.mybeaconapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.skybeacon.sdk.RangingBeaconsListener;
import com.skybeacon.sdk.ScanServiceStateCallback;
import com.skybeacon.sdk.locate.SKYBeaconManager;
import com.skybeacon.sdk.locate.SKYRegion;

import java.util.List;

public class BluetoothTestActivity extends AppCompatActivity {
    private static final SKYRegion ALL_SEEKCY_BEACONS_REGION = new SKYRegion("rid_all", null, null, null, null);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);
        SKYBeaconManager.getInstance().init(this);
        SKYBeaconManager.getInstance().setRangingBeaconsListener(new RangingBeaconsListener() {
            @Override
            public void onRangedBeacons(SKYRegion skyRegion, List list) {
                Log.e("test", "onRangedBeacons=" + skyRegion.getDeviceAddress());
            }

            @Override
            public void onRangedBeaconsMultiIDs(SKYRegion skyRegion, List list) {
                Log.e("test", "onRangedBeaconsMultiIDs=" + skyRegion.getDeviceAddress());
            }

            @Override
            public void onRangedNearbyBeacons(SKYRegion skyRegion, List list) {
                Log.e("test", "onRangedNearbyBeacons=" + skyRegion.getDeviceAddress());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startRanging();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRanging();
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
}
