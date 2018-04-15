package com.example.golden.aviationrc;


import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.OnConnectionClosedListener;

/**
 * Created by Golden on 3/28/2018.
 */

public class ReceivingTask extends AsyncTask<Void, String, Void> {
    private BluetoothConnection mConnection;
    private OnMessageReceivedListener listener;
    private char[] buffer = new char[1024];

    public ReceivingTask(BluetoothConnection connection, OnMessageReceivedListener listener) {
        mConnection = connection;
        connection.addOnClosedListener(new OnConnectionClosedListener() {
            @Override
            public void onConnectionClosed(BluetoothDevice device) {
                cancel(false);
            }
        });
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while (!isCancelled()) {
            int len = mConnection.read(buffer);
            if (len > 0) {
                publishProgress(new String(buffer));
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (listener != null)
            listener.onMessageReceived(values[0], mConnection, this);
    }
}
