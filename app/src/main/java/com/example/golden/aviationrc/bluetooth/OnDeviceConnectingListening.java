package com.example.golden.aviationrc.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Golden on 3/27/2018.
 */

public interface OnDeviceConnectingListening {
    void onDeviceConnecting(BluetoothDevice device);
}
