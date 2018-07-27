package demo.jmy.com.mybeaconapplication.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import demo.jmy.com.mybeaconapplication.bean.BeaconInfo
import demo.jmy.com.mybeaconapplication.bean.SendInfoBean
import demo.jmy.com.mybeaconapplication.bean.iBeaconClass
import demo.jmy.com.mybeaconapplication.util.DataSendUtil
import demo.jmy.com.mybeaconapplication.util.MyLogUtil
import demo.jmy.com.mybeaconapplication.util.SystemInfoUtil
import java.util.*
import kotlin.collections.ArrayList

class MyService : Service() {

    private var imei: String? = null
    private var id: String? = null
    private var isStart = false
    private var isStartUpLoad = false

    lateinit var mBluetoothAdapter: BluetoothAdapter

    private val beaconInfoList = ArrayList<BeaconInfo>()

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
                while (isStart) {
                    try {
                        searchBluetooth()
                        Thread.sleep(10 * 1000)
                    } catch (e: Exception) {
                        MyLogUtil.writeCustomLog(e.toString())
                        Log.e("test", e.toString())
                    }
                    try {
                        stopSearchBluetooth()
                        beaconInfoList.clear()
                    } catch (e: Exception) {
                        MyLogUtil.writeCustomLog(e.toString())
                        Log.e("test", e.toString())
                    }
                }
            }
        }).start()
    }

    private fun startUpLoad() {
        var timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                try {
                    var copyList = beaconInfoList.clone() as ArrayList<BeaconInfo>
                    var sendData = ArrayList<SendInfoBean.BeaconInfo>()

                    Log.e("test","--------------------")
                    for(ss in copyList){
                        Log.e("test",ss.toString())
                    }
                    Log.e("test","--------------------")
                    if (copyList.size >= 3) {
                        var count = 0
                        for (d in copyList) {
                            if (count >= 3) {
                                break
                            }
                            count++
                            var data = SendInfoBean.BeaconInfo(d.getiBeacon().major, d.getiBeacon().minor, d.rssi, d.getiBeacon().txPower)
                            sendData.add(data)
                        }
                        if (sendData.size != 0) {
                            var msg = listToJson(sendData)
                            DataSendUtil.sendData(msg.toByteArray())
                        }
                    }
                } catch (e: Exception) {
                    MyLogUtil.writeCustomLog(e.toString())
                    Log.e("test", e.toString())
                }
            }
        }, 0, 1000)
    }

    private fun listToJson(sendData: ArrayList<SendInfoBean.BeaconInfo>): String {
        var data = SendInfoBean()
        data.id = id
        data.data = sendData
        return Gson().toJson(data)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSearchBluetooth()
    }

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
            return
        }
        if (!mBluetoothAdapter.isEnabled) {
            Log.e("test", "请打开蓝牙")
            return
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback)
        if (!isStartUpLoad) {
            isStartUpLoad = true
            startUpLoad()
        }
        /*mBluetoothAdapter.bluetoothLeScanner.startScan(object : ScanCallback(){
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
            }
        })*/
    }

    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        val ibeacon = iBeaconClass.fromScanData(device, rssi, scanRecord)
        if (!addDevice(ibeacon)) {
            return@LeScanCallback
        }
        Collections.sort(beaconInfoList, object : Comparator<BeaconInfo> {
            override fun compare(h1: BeaconInfo, h2: BeaconInfo): Int {
                return (h2.currentTime - h1.currentTime).toInt()
            }
        })
        //
    }

    private fun addDevice(device: iBeaconClass.iBeacon?): Boolean { //更新beacon信息
        if (device == null) {
            Log.d("DeviceScanActivity ", "device==null ")
            return false
        }
        if (device.major != 20000 && device.major != 10002) {
            return false
        }
        /*if (device.major != 10002 && device.major != 3333 && device.major != 1020) {
            return false
        }
        if (device.minor != 5209 && device.minor != 5207 && device.minor != 4080) {
            return false
        }*/
        for (i in beaconInfoList.indices) {
            val btAddress = beaconInfoList[i].getiBeacon().bluetoothAddress
            if (btAddress == device.bluetoothAddress) {
                beaconInfoList[i].rssi = device.rssi
                beaconInfoList[i].currentTime = System.currentTimeMillis()
                return true
            }
        }
        var beaconInfo = BeaconInfo()
        beaconInfo.setiBeacon(device)
        beaconInfo.rssi = device.rssi
        beaconInfo.currentTime = System.currentTimeMillis()
        beaconInfoList.add(beaconInfo)
        return true
    }
}