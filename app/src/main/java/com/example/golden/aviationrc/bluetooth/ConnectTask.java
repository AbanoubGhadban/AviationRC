package com.example.golden.aviationrc.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Golden on 3/25/2018.
 */

public class ConnectTask extends AsyncTask<Void, Void, Void> {
    private BluetoothSocket socket;
    private OnDeviceConnectedListener mDeviceConnectedListener;
    private BluetoothDevice mDevice;
    private UUID uuid;

    ConnectTask(BluetoothDevice device, UUID uuid, OnDeviceConnectedListener listener) {
        this.uuid = uuid;
        mDeviceConnectedListener = listener;
        mDevice = device;
    }

    @Override
    protected void onPreExecute() {
        try {
            socket = mDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (socket == null)
            return null;

        if (!isCancelled()) {
            try {
                socket.connect();
            } catch (IOException e) {
                Log.e("Connecting to server", "Cloud not Connect to Device");
                try {
                    socket.close();
                } catch (IOException e1) {
                    Log.e("Connecting to server", "Could not Close Socket after error");
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        BluetoothConnection connection = null;
        if (socket != null || socket.isConnected()) {
            if (isCancelled()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("Connection Task", "Could not close Socket after cancellation");
                }
            } else
                try {
                    connection = new BluetoothConnection(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    connection = null;
                    try {
                        socket.close();
                    } catch (Exception ex) {
                    }
                }
        }

        if (!isCancelled())
            mDeviceConnectedListener.onDeviceConnected(connection);
    }
}
