package com.example.golden.aviationrc;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.golden.aviationrc.bluetooth.BluetoothHelper;

import java.util.Set;

/**
 * Created by Golden on 3/26/2018.
 */

public class DevicesListViewAdapter extends BaseAdapter {
    Context mContext;
    Set<BluetoothDevice> mDevices;
    LayoutInflater mInflater;

    public DevicesListViewAdapter(Context context, Set<BluetoothDevice> devices) {
        mContext = context;
        mDevices = devices;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return Global.getDeviceFromSet(mDevices, i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflater.inflate(R.layout.layout_devices_list_view_item, viewGroup, false);
        BluetoothDevice device = Global.getDeviceFromSet(mDevices, i);

        CheckedTextView deviceNameTV = view.findViewById(R.id.device_name_tv);
        TextView deviceMacTV = view.findViewById(R.id.device_mac_tv);

        deviceNameTV.setText(device.getName());
        deviceMacTV.setText(device.getAddress());

        if (BluetoothHelper.isConnected()) {
            BluetoothDevice activeDevice = BluetoothHelper.getConnection(0).getRemoteDevice();
            if (activeDevice.equals(device)) {
                TextView connectedTV = view.findViewById(R.id.connected_tv);
                connectedTV.setText("Connected");
                connectedTV.setVisibility(View.VISIBLE);
            }
        }

        if (BluetoothHelper.isConnecting()) {
            if (BluetoothHelper.isConnecting(device)) {
                TextView connectedTV = view.findViewById(R.id.connected_tv);
                connectedTV.setText("Connecting..");
                connectedTV.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }
}
