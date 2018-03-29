package com.example.golden.aviationrc.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.LinkedList;

/**
 * Created by Golden on 3/25/2018.
 */

public class BluetoothConnection {
    private BluetoothSocket mSocket;
    private InputStreamReader reader;
    private OutputStreamWriter writer;

    private LinkedList<OnConnectionClosedListener> mClosedListeners = new LinkedList<>();
    private void fireClosedListeners(){
        for (OnConnectionClosedListener listener:mClosedListeners) {
            listener.onConnectionClosed(mSocket.getRemoteDevice());
        }
        mClosedListeners.clear();
    }

    public void addOnClosedListener(OnConnectionClosedListener listener) {
        mClosedListeners.add(listener);
    }

    public void removeOnClosedListener(OnConnectionClosedListener listener) {
        mClosedListeners.remove(listener);
    }

    public BluetoothConnection (BluetoothSocket socket) throws IOException {
        mSocket = socket;
        try {
            reader = new InputStreamReader(socket.getInputStream());
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            throw new IOException("Could not get Input/Output Streams from Socket", e);
        }
    }

    public boolean isConnected(){return mSocket.isConnected();}

    public void close(){
        try {
            mSocket.close();
            reader.close();
            writer.close();
        } catch (IOException e) {
            Log.e("BluetoothConnection", "Could not colse Socket");
        }
        fireClosedListeners();
        Log.e("Connection", "Close Function");
    }

    public void close(OnConnectionClosedListener onConnectionClosedListener) {
        close();
        if (onConnectionClosedListener != null)
            onConnectionClosedListener.onConnectionClosed(mSocket.getRemoteDevice());
    }

    public BluetoothDevice getRemoteDevice(){return mSocket.getRemoteDevice();}

    public void write(int c) {
        try {
            writer.write(c);
        } catch (IOException e) {
            close();
            Log.e("Connection", "write Function");
//            throw new IOException("Could not write in Output Stream", e);
        }
    }

    public void write(String str) {
        try {
            writer.write(str);
        } catch (IOException e) {
            close();
            Log.e("Connection", "write Function");
//            throw new IOException("Could not write in Output Stream", e);
        }
    }

    public void write(String str, int off, int len) {
        try {
            writer.write(str, off, len);
        } catch (IOException e) {
//            throw new IOException("Could not write in Output Stream", e);
        }
    }

    public void write(char[] cbuf) {
        try {
            writer.write(cbuf);
        } catch (IOException e) {
            close();
            Log.e("Connection", "write Function");
//            throw new IOException("Could not write in Output Stream", e);
        }
    }

    public void write(char[] cbuf, int off, int len) {
        try {
            writer.write(cbuf, off, len);
        } catch (IOException e) {
            close();
            Log.e("Connection", "write Function");
//            throw new IOException("Could not write in Output Stream", e);
        }
    }

    public void flush() {
        try {
            writer.flush();
        } catch (IOException e) {
            close();
            Log.e("Connection", "flush Function");
//            throw new IOException("Could not flush data in Output Stream", e);
        }
    }

    public boolean readyToRead() {
        try {
            return reader.ready();
        } catch (IOException e) {
            close();

            Log.e("Connection", "ready Function");
//            throw new IOException("Problem with ready function", e);
            return false;
        }
    }

    public long skip(long l) throws IOException {
        try {
            return reader.skip(l);
        } catch (IOException e) {
            throw new IOException("Problem with skip function", e);
        }
    }

    public int read() {
        try {
            return reader.read();
        } catch (IOException e) {
            close();
//            throw new IOException("Problem with read function", e);
        }
        return -1;
    }

    public int read(char[] cbuf) {
        try {
            return reader.read(cbuf);
        } catch (IOException e) {
            close();
//            throw new IOException("Problem with read function", e);
            return -1;
        }
    }

    public int read(char[] cbuf, int off, int len) {
        try {
            return reader.read(cbuf, off, len);
        } catch (IOException e) {
            close();
//            throw new IOException("Problem with read function", e);
            return -1;
        }
    }

    public int read(CharBuffer target) {
        try {
            return reader.read(target);
        } catch (IOException e) {
            close();
//            throw new IOException("Problem with read function", e);
            return -1;
        }
    }
}
