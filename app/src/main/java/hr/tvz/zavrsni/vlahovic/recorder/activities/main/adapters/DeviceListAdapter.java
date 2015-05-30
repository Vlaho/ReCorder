package hr.tvz.zavrsni.vlahovic.recorder.activities.main.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import hr.tvz.zavrsni.vlahovic.recorder.R;
import hr.tvz.zavrsni.vlahovic.recorder.activities.main.MyDevice;

/**
 * Created by Vlaho on 24.5.2015..
 */
public class DeviceListAdapter extends BaseAdapter {

    private Context context;
    private List<MyDevice> data;

    public DeviceListAdapter(Context context, List<MyDevice> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MyDevice getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.device_list_item, viewGroup, false);
        }

        MyDevice device = data.get(position);

        TextView hostNameText = (TextView) view.findViewById(R.id.hostNameText);
        hostNameText.setText(device.getHostName());
        TextView addressText = (TextView) view.findViewById(R.id.addressText);
        addressText.setText(device.getAddress());


        return view;
    }
}
