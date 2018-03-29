package com.example.golden.aviationrc;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;

/**
 * Created by Golden on 3/28/2018.
 */

public interface OnMessageReceivedListener {
    void onMessageReceived(String message, BluetoothConnection connection, ReceivingTask task);
}
