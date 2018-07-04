package demo.jmy.com.mybeaconapplication

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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


class Main2Activity : AppCompatActivity() {
    var permissions = arrayOf(Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

    lateinit var mBluetoothAdapter: BluetoothAdapter

    var data = ArrayList<BluetoothDevice>()

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var adapter = MyBluetooth1Adapter(data, this)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        btn.setOnClickListener {
            searchBluetooth()
        }
        for (p in permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    this.requestPermissions(permissions, 100)
                    return
                }
            }
        }
    }

    private fun searchBluetooth() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return
        }
        startDiscovery()
    }

    /**
     * 广播接收器
     */
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // 收到的广播类型
            val action = intent.action
            // 发现设备的广播
            if (BluetoothDevice.ACTION_FOUND == action) {
                // 从intent中获取设备
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (!data.contains(device)) {
                    data.add(device)
                }
                rv.adapter!!.notifyDataSetChanged()
                // 搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                // 关闭进度条
                // 关闭进度条
                progressDialog!!.dismiss()
                Log.e("test", "onReceive: 搜索完成")
                rv.adapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * 注册异步搜索蓝牙设备的广播
     */
    private fun startDiscovery() {
        // 找到设备的广播
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        // 注册广播
        registerReceiver(receiver, filter)
        // 搜索完成的广播
        val filter1 = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        // 注册广播
        registerReceiver(receiver, filter1)
        Log.e("test", "startDiscovery: 注册广播")
        startScanBluth()
    }

    /**
     * 搜索蓝牙的方法
     */
    private fun startScanBluth() {
        // 判断是否在搜索,如果在搜索，就取消搜索
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery()
        }
        // 开始搜索
        mBluetoothAdapter.startDiscovery()
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
        }
        progressDialog!!.setMessage("正在搜索，请稍后！")
        progressDialog!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiver != null) {
            unregisterReceiver(receiver)
        }
    }
}