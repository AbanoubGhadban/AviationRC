package com.example.golden.aviationrc.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Golden on 3/25/2018.
 */

class ListenTask extends android.os.AsyncTask<Void, Void, Void> {
    UUID uuid;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private BluetoothAdapter mBluetoothAdapter;
    private OnDeviceConnectedListener mDeviceConnectedListener;
    private String name;

    ListenTask(BluetoothAdapter adapter, String name, UUID uuid, OnDeviceConnectedListener listener) {
        mBluetoothAdapter = adapter;
        this.name = name;
        this.uuid = uuid;
        mDeviceConnectedListener = listener;
    }

    @Override
    protected void onPreExecute() {
        try {
            serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(name, uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (serverSocket == null)
            return null;

        while (socket == null && !isCancelled()) {
            try {
                socket = serverSocket.accept(1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        BluetoothConnection connection;
        if (socket == null)
            connection = null;
        else
            try {
                connection = new BluetoothConnection(socket);
            } catch (IOException e) {
                e.printStackTrace();
                connection = null;
            }
        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.e("Listen Task", "Could not Close Server Socket");
        }

        mDeviceConnectedListener.onDeviceConnected(connection);
    }
}
