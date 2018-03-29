package com.example.golden.aviationrc.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Golden on 3/28/2018.
 */

public interface OnConnectionCancelledListener {
    void onConnectionCancelled(BluetoothDevice device);
}
