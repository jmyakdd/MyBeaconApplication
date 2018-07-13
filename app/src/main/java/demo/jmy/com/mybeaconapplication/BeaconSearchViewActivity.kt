package demo.jmy.com.mybeaconapplication

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class BeaconSearchViewActivity : AppCompatActivity() {
    var permissions = arrayOf(Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION)
    lateinit var mBluetoothAdapter: BluetoothAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var adapter = MyBluetoothAdapter(mLeDevices, this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

        for (p in permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(permissions, 100)
                    return
                }
            }
        }
        searchBluetooth()
        initReceiver()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            for (i in grantResults) {
                if (i == PackageManager.PERMISSION_GRANTED) {
                    continue
                } else {
                    return
                }
            }
            searchBluetooth()
            initReceiver()
        }
    }

    var receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            var action = p1!!.action as String
            when (action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    var bluetoothState = p1.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
                    when (bluetoothState) {
                        BluetoothAdapter.STATE_TURNING_ON -> {
                            Log.e("test", "打开蓝牙中...")
                        }
                        BluetoothAdapter.STATE_ON -> {
                            Log.e("test", "已打开蓝牙")
                            searchBluetooth()
                        }
                        BluetoothAdapter.STATE_TURNING_OFF -> {
                            Log.e("test", "关闭蓝牙中...")
                            stopSearchBluetooth()
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            Log.e("test", "已关闭蓝牙")
                        }
                    }
                }
            }
        }
    }

    private fun stopSearchBluetooth() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback)
        }
    }

    private fun initReceiver() {
        var filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null)
            unregisterReceiver(receiver)
    }

    fun searchBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备没有蓝牙模块", Toast.LENGTH_SHORT).show()
            return
        }
        if (!mBluetoothAdapter.isEnabled) {
            Toast.makeText(this, "请打开蓝牙", Toast.LENGTH_SHORT).show()
            return
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback)
    }
    private var count = 0
    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        val ibeacon = iBeaconClass.fromScanData(device, rssi, scanRecord)
        if (!addDevice(ibeacon)) {
            return@LeScanCallback
        }
        Collections.sort(mLeDevices, object : Comparator<iBeaconClass.iBeacon> {
            override fun compare(h1: iBeaconClass.iBeacon, h2: iBeaconClass.iBeacon): Int {
                return h2.rssi - h1.rssi
            }
        })
        rv.adapter!!.notifyDataSetChanged()
        Log.e("test",count.toString())
        count++
    }

    private val mLeDevices = ArrayList<iBeaconClass.iBeacon>()
    private fun addDevice(device: iBeaconClass.iBeacon?): Boolean { //更新beacon信息
        if (device == null) {
            Log.d("DeviceScanActivity ", "device==null ")
            return false
        }
        if (device.major != 10002 && device.major != 3333) {
            return false
        }
        if(device.minor != 5209 && device.minor != 5207){
            return false
        }
        for (i in mLeDevices.indices) {
            val btAddress = mLeDevices[i].bluetoothAddress
            if (btAddress == device.bluetoothAddress) {
                mLeDevices.set(i, device)
                return true
            }
        }
        mLeDevices.add(device)
        return true
    }

}