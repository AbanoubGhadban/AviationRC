package com.example.golden.aviationrc;

import android.bluetooth.BluetoothDevice;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.BluetoothHelper;
import com.example.golden.aviationrc.bluetooth.OnConnectionClosedListener;
import com.example.golden.aviationrc.bluetooth.OnDeviceConnectedListener;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Golden on 3/26/2018.
 */

public class Global {
    public static final UUID UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
//    fa87c0d0-afac-11de-8a39-0800200c9a66
    public static final UUID UUID_INSECURE =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public static BluetoothDevice getDeviceFromSet(Set<BluetoothDevice> devices, int index) {
        int i = 0;
        for (Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext();){
            if (i++ == index)
                return it.next();
            else
                it.next();
        }
        return null;
    }

    public static int getDeviceIndexFromSet(Set<BluetoothDevice> devices, BluetoothDevice device) {
        int i = 0;
        for (Iterator<BluetoothDevice> it = devices.iterator(); it.hasNext();){
            if (it.next().equals(device))
                return i;
            else
                i++;
        }
        return -1;
    }

    static {
        BluetoothHelper.setMaxConnections(1);
    }
}
