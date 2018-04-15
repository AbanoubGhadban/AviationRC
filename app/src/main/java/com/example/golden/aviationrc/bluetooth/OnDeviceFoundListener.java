package com.example.golden.aviationrc.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Golden on 3/25/2018.
 */

public interface OnDeviceFoundListener {
    void onDeviceFound(BluetoothDevice device);
}
