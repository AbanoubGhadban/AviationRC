package com.example.golden.aviationrc;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.example.golden.aviationrc.Global;
import com.example.golden.aviationrc.R;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Golden on 3/26/2018.
 */

public class DevicesSpinnerAdapter extends ArrayAdapter<BluetoothDevice> {
    private Set<BluetoothDevice> mDevices;
    private Context mContext;
    private LayoutInflater mInflater;

    public DevicesSpinnerAdapter(Context context, Set<BluetoothDevice> devices) {
        super(context, R.layout.layout_devices_spinner);
        mContext = context;
        mDevices = devices;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDevices.size();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        return Global.getDeviceFromSet(mDevices, i);
    }

    @Override
    public long getItemId(int i) {
        return Global.getDeviceFromSet(mDevices, i).getAddress().hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mInflater.inflate(R.layout.layout_devices_spinner, viewGroup, false);
        TextView deviceNameTV = view.findViewById(R.id.device_name_tv);
        deviceNameTV.setText(Global.getDeviceFromSet(mDevices, i).getName());
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.layout_devices_spinner_drop_down, parent, false);
        CheckedTextView deviceNameTV = convertView.findViewById(R.id.device_name_tv);

        BluetoothDevice device = Global.getDeviceFromSet(mDevices, position);
        deviceNameTV.setText(device.getName());
        TextView deviceMacTV = convertView.findViewById(R.id.device_mac_tv);
        deviceMacTV.setText(device.getAddress());
        return convertView;
    }
}
