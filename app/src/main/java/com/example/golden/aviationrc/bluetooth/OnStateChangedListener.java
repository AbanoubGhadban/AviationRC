package com.example.golden.aviationrc.bluetooth;

/**
 * Created by Golden on 3/25/2018.
 */

public interface OnStateChangedListener {
    void onBluetoothStateChanged(BluetoothState state, BluetoothState prevState);
}
