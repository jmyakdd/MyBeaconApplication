package demo.jmy.com.mybeaconapplication.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import demo.jmy.com.mybeaconapplication.R;

public class MyBluetooth1Adapter extends RecyclerView.Adapter<MyBluetooth1Adapter.ViewHolder> {
    private List<BluetoothDevice> data;
    private Context context;

    public MyBluetooth1Adapter(List<BluetoothDevice> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bluetooth, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String name = data.get(i).getName();
        viewHolder.name.setText(name != null && !name.trim().equals("") ? name : "未知设备");

        //详细参考：http://blog.csdn.net/mirkowu/article/details/53862842
        BluetoothDevice blueDevice = data.get(i);
        //设备名称
        //设备的蓝牙地（地址为17位，都为大写字母-该项貌似不可能为空）
        String deviceAddress = blueDevice.getAddress();
        viewHolder.name.append(" " + deviceAddress);
        //设备的蓝牙设备类型（DEVICE_TYPE_CLASSIC 传统蓝牙 常量值：1, DEVICE_TYPE_LE  低功耗蓝牙 常量值：2
        //DEVICE_TYPE_DUAL 双模蓝牙 常量值：3. DEVICE_TYPE_UNKNOWN：未知 常量值：0）
        int deviceType = blueDevice.getType();
        if (deviceType == 0) {
            viewHolder.name.append("  未知类型");
        } else if (deviceType == 1) {
            viewHolder.name.append("  传统蓝牙");
        } else if (deviceType == 2) {
            viewHolder.name.append("  低功耗蓝牙");
        } else if (deviceType == 3) {
            viewHolder.name.append("  双模蓝牙");
        }
        //设备的状态（BOND_BONDED：已绑定 常量值：12, BOND_BONDING：绑定中 常量值：11, BOND_NONE：未匹配 常量值：10）
        int deviceState = blueDevice.getBondState();
        if (deviceState == 10) {
            viewHolder.name.append("  未匹配");
        } else if (deviceState == 11) {
            viewHolder.name.append("  绑定中");
        } else if (deviceState == 12) {
            viewHolder.name.append("  已绑定");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }
}
