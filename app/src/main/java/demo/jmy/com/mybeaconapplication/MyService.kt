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

    private var imei: String? = null
    private var id: String? = null
    private var isStart = false

    private var handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg != null) {
                Toast.makeText(this@MyService, msg.obj.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isStart = true

        imei = SystemInfoUtil.getIMEI(this)
        id = SystemInfoUtil.getDeviceId(imei)

        Thread(object : Runnable {
            override fun run() {
                startUpLoad()
                while (isStart) {
                    searchBluetooth()
                    try {
                        Thread.sleep(10 * 1000)
                    } catch (e: Exception) {

                    }
                    stopSearchBluetooth()
                    try {
                        Thread.sleep(1 * 1000)
                    } catch (e: Exception) {

                    }
                }
            }
        }).start()
    }

    private fun startUpLoad() {
        var timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                synchronized(mLeDevices) {
                    var msg = id + ":\n"
                    for (d in mLeDevices) {
                        msg += d.major.toString() + " " + d.minor + " " + d.rssi + " " + d.txPower + "\n"
                    }
                    if (msg != null && !msg.equals(""))
                        DataSendUtil.sendData(msg.toByteArray())
//                    handMessage("收到设备：" + msg)
                    Log.e("test", "收到设备：" + " " + msg)
//                    MyLogUtil.writeCustomLog("收到设备：" + msg)
                    mLeDevices.clear()
                }
            }
        }, 0, 2000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSearchBluetooth()
    }

    lateinit var mBluetoothAdapter: BluetoothAdapter

    private fun stopSearchBluetooth() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback)
        }
    }

    fun searchBluetooth() {
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
        if (device.major != 10002 && device.major != 3333 && device.major != 1020) {
            return false
        }
        if (device.minor != 5209 && device.minor != 5207 && device.minor != 4080) {
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