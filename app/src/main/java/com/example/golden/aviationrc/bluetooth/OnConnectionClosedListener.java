package com.example.golden.aviationrc.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Golden on 3/26/2018.
 */

public interface OnConnectionClosedListener {
    void onConnectionClosed(BluetoothDevice device);
}
