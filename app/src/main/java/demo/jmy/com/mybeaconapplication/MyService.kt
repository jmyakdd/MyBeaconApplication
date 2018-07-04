package demo.jmy.com.mybeaconapplication

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.widget.Toast
import java.util.*

class MyService : Service() {
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg != null) {
                Toast.makeText(this@MyService, msg.obj.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("test", "onCreate")
//        initReceiver()
        Thread(object : Runnable {
            override fun run() {
                while (true) {
                    try {
                        Thread.sleep(10 * 1000)
                    } catch (e: Exception) {

                    }
                    searchBluetooth()
                }
            }
        }).start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        MyLogUtil.writeCustomLog("service destroy")
//        unregisterReceiver(receiver)
    }

    /*private fun initReceiver() {
        var filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver, filter)
    }*/

    /*var receiver = object : BroadcastReceiver() {
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
    }*/

    lateinit var mBluetoothAdapter: BluetoothAdapter

    private fun stopSearchBluetooth() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback)
            mLeDevices.clear()
        }
    }

    fun searchBluetooth() {
        var timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                var msg = ""
                for (d in mLeDevices) {
                    msg += d.major.toString() + " "+d.minor+" "+d.rssi+" "+d.txPower+"\n"
                }
                handMessage("收到设备：" + msg)
                MyLogUtil.writeCustomLog("收到设备：" + msg)
                stopSearchBluetooth()
            }
        }, 7000)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
            MyLogUtil.writeCustomLog("设备没有蓝牙模块")
//            Toast.makeText(this,"设备没有蓝牙模块", Toast.LENGTH_SHORT).show()
            return
        }
        if (!mBluetoothAdapter.isEnabled) {
            Log.e("test", "请打开蓝牙")
            return
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback)
    }

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
        //
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

    fun handMessage(msg: String) {
        var message = Message()
        message.what = 0
        message.obj = msg
        handler.sendMessage(message)
    }
}