package demo.jmy.com.mybeaconapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyBluetoothAdapter extends RecyclerView.Adapter<MyBluetoothAdapter.ViewHolder> {
    private List<iBeaconClass.iBeacon>data;
    private Context context;

    public MyBluetoothAdapter(List<iBeaconClass.iBeacon> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bluetooth,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        viewHolder.name.setText(data.get(i).name==null||data.get(i).name.equals("")?"未知设备":data.get(i).name);
        viewHolder.name.setText(data.get(i).toString());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
        }
    }
}
