package com.example.golden.aviationrc.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Golden on 3/25/2018.
 */

public class BluetoothHelper {
    private static final HashSet<OnStateChangedListener> mBTStateChangedListeners = new HashSet<>();
    private static final HashSet<OnConnectionStateChangedListener> mBTConnectionStateListeners = new HashSet<>();
    private static final HashSet<OnDeviceFoundListener> mDeviceFoundListeners = new HashSet<>();
    private static final HashSet<OnSearchingFinishedListener> mSearchingFinishedListeners = new HashSet<>();
    private static final HashSet<OnSearchingStartedListener> mSearchingStartedListeners = new HashSet<>();
    private static final HashSet<OnDeviceConnectingListening> mDeviceConnectingListeners = new HashSet<>();
    private static final HashSet<OnConnectionCancelledListener> mConnectionCancelledListeners = new HashSet<>();
    private static final LinkedList<BluetoothConnection> mConnections = new LinkedList<>();
    private static final HashSet<OnDeviceConnectedListener> mDeviceConnectedListeners = new HashSet<>();
    private static final HashSet<OnConnectionClosedListener> mConnectionClosedListeners = new HashSet<>();
    private static final HashMap<BluetoothDevice, ConnectTask> mConnectingDevices = new HashMap<>();
    private static BluetoothAdapter mBluetoothAdapter;
    private static ListeningInfo mListeningInfo;
    private static boolean isSearching;
    private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    int prevState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0);
                    fireBTStateChangedListeners(getBluetoothState(state), getBluetoothState(prevState));
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    fireDeviceFoundListeners(device);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    isSearching = false;
                    fireSearchingFinishedListeners();
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    isSearching = true;
                    fireOnSearchStartedListeners();
                    break;
            }
        }
    };
    private static int maxConnections = -1;
    private static ListenTask listenTask;

    static {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private BluetoothHelper() {
    }

    private static void fireBTStateChangedListeners(BluetoothState state, BluetoothState prevState) {
        for (OnStateChangedListener listener : mBTStateChangedListeners) {
            listener.onBluetoothStateChanged(state, prevState);
        }
    }

    private static void fireBTConnectionStateListeners(ConnectionState state, ConnectionState prevState) {
        for (OnConnectionStateChangedListener listener : mBTConnectionStateListeners) {
            listener.onBluetoothConnectionStateChanged(state, prevState);
        }
    }

    private static void fireDeviceFoundListeners(BluetoothDevice device) {
        for (OnDeviceFoundListener listener : mDeviceFoundListeners) {
            listener.onDeviceFound(device);
        }
    }

    private static void fireSearchingFinishedListeners() {
        for (OnSearchingFinishedListener listener : mSearchingFinishedListeners) {
            listener.onStopSearching();
        }
    }

    private static void fireOnSearchStartedListeners() {
        for (OnSearchingStartedListener listener : mSearchingStartedListeners) {
            listener.onSearchingStarted();
        }
    }

    private static void fireOnDeviceConnectingListeners(BluetoothDevice device) {
        for (OnDeviceConnectingListening listener : mDeviceConnectingListeners) {
            listener.onDeviceConnecting(device);
        }
    }

    private static void fireOnConnectionCancelledListeners(BluetoothDevice device) {
        for (OnConnectionCancelledListener listener : mConnectionCancelledListeners) {
            listener.onConnectionCancelled(device);
        }
    }

    private static void addConnection(final BluetoothConnection connection) {
        if (connection != null) {
            if (maxConnections == mConnections.size()) {
                Log.e("Close First", "Close First");
                mConnections.getFirst().close();
            }

            mConnections.add(connection);

            connection.addOnClosedListener(new OnConnectionClosedListener() {
                @Override
                public void onConnectionClosed(BluetoothDevice device) {
                    Log.e("Connection Closed", device.getName() + " Closed Connection");
                    mConnections.remove(connection);
                    for (OnConnectionClosedListener listener : mConnectionClosedListeners) {
                        listener.onConnectionClosed(connection.getRemoteDevice());
                    }
                }
            });
        }

        for (OnDeviceConnectedListener listener : mDeviceConnectedListeners) {
            listener.onDeviceConnected(connection);
        }
    }

    private static BluetoothConnection getConnectionByDevice(BluetoothDevice device) {
        for (BluetoothConnection con : mConnections) {
            if (con.getRemoteDevice().equals(device))
                return con;
        }
        return null;
    }

    public static void addOnDeviceConnectedListener(OnDeviceConnectedListener listener) {
        mDeviceConnectedListeners.add(listener);
    }

    public static void addOnConnectionClosedListener(OnConnectionClosedListener listener) {
        mConnectionClosedListeners.add(listener);
    }

    public static void removeOnDeviceConnectedListenerListener(OnDeviceConnectedListener listener) {
        mDeviceConnectedListeners.remove(listener);
    }

    public static void removeOnConnectionClosedListener(OnConnectionClosedListener listener) {
        mConnectionClosedListeners.remove(listener);
    }

    public static void removeAllListeners() {
        mDeviceFoundListeners.clear();
        mSearchingFinishedListeners.clear();
        mSearchingStartedListeners.clear();
        mDeviceConnectedListeners.clear();
        mConnectionClosedListeners.clear();
        mBTStateChangedListeners.clear();
        mDeviceConnectingListeners.clear();
        mConnectionCancelledListeners.clear();
    }

    public static void removeAllListeners(Object listener) {
        mDeviceFoundListeners.remove(listener);
        mSearchingFinishedListeners.remove(listener);
        mSearchingStartedListeners.remove(listener);
        mDeviceConnectedListeners.remove(listener);
        mConnectionClosedListeners.remove(listener);
        mBTStateChangedListeners.remove(listener);
        mDeviceConnectingListeners.remove(listener);
        mConnectionCancelledListeners.remove(listener);
    }

    public static BluetoothConnection[] getAllConnections() {
        BluetoothConnection[] cons = new BluetoothConnection[mConnections.size()];
        for (int i = 0; i < mConnections.size(); i++) {
            cons[i] = mConnections.get(i);
        }
        return cons;
    }

    public static BluetoothConnection getConnection(int index) {
        if (index < mConnections.size())
            return mConnections.get(index);
        else
            return null;
    }

    public static void setMaxConnections(int max) {
        if (max <= 0)
            maxConnections = -1;
        else
            maxConnections = max;
    }

    private static BluetoothState getBluetoothState(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_TURNING_ON:
                return BluetoothState.STATE_TURNING_ON;
            case BluetoothAdapter.STATE_ON:
                return BluetoothState.STATE_ON;
            case BluetoothAdapter.STATE_TURNING_OFF:
                return BluetoothState.STATE_TURNING_OFF;
            case BluetoothAdapter.STATE_OFF:
                return BluetoothState.STATE_OFF;
        }
        return null;
    }

    public static void prepareBluetooth(Context context) {
        registerReceivers(context);
        if (mBluetoothAdapter == null) {
            throw new UnsupportedOperationException("This device does not support Bluetooth");
        }
    }

    private static void registerReceivers(Context context) {
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        context.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private static void unregisterReceivers(Context context) {
        try {
            context.unregisterReceiver(mReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    public static boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public static void addOnConnectionStateChangedListener(OnConnectionStateChangedListener listener) {
        mBTConnectionStateListeners.add(listener);
    }

    public static void removeOnConnectionStateChangedListener(OnConnectionStateChangedListener listener) {
        mBTConnectionStateListeners.remove(listener);
    }

    public static void addOnStateChangedListener(OnStateChangedListener listener) {
        mBTStateChangedListeners.add(listener);
    }

    public static void removeOnStateChangedListener(OnStateChangedListener listener) {
        mBTStateChangedListeners.remove(listener);
    }

    public static void addOnDeviceConnectingListener(OnDeviceConnectingListening listener) {
        mDeviceConnectingListeners.add(listener);
    }

    public static void removeOnDeviceConnectingListener(OnDeviceConnectingListening listener) {
        mDeviceConnectingListeners.remove(listener);
    }

    public static void addOnDeviceFoundListener(OnDeviceFoundListener listener) {
        mDeviceFoundListeners.add(listener);
    }

    public static void removeOnDeviceFoundListener(OnDeviceFoundListener listener) {
        mDeviceFoundListeners.remove(listener);
    }

    public static void addOnSearchingStartedListener(OnSearchingStartedListener listener) {
        mSearchingStartedListeners.add(listener);
    }

    public static void removeOnSearchingStartedListener(OnSearchingStartedListener listener) {
        mSearchingStartedListeners.remove(listener);
    }

    public static void addOnSearchingFinishedListener(OnSearchingFinishedListener listener) {
        mSearchingFinishedListeners.add(listener);
    }

    public static void removeOnSearchingFinishedListener(OnSearchingFinishedListener listener) {
        mSearchingFinishedListeners.remove(listener);
    }

    public static void addOnConnectionCancelledListener(OnConnectionCancelledListener listener) {
        mConnectionCancelledListeners.add(listener);
    }

    public static void removeOnConnectionCancelledListener(OnConnectionCancelledListener listener) {
        mConnectionCancelledListeners.remove(listener);
    }

    public static void enableBluetooth(Context context) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void disableBluetooth() {
        mBluetoothAdapter.disable();
    }

    public static void makeDiscoverable(Context context) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void makeDiscoverable(Context context, int duration) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Set<BluetoothDevice> getBonedDevices() {
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        HashSet<BluetoothDevice> convertedDevices = new HashSet<>(devices.size());
        for (BluetoothDevice device : devices) {
            convertedDevices.add(device);
        }
        return convertedDevices;
    }

    public static void startSearching() {
        stopSearching();
        mBluetoothAdapter.startDiscovery();
    }

    public static void stopSearching() {
        mBluetoothAdapter.cancelDiscovery();
    }

    public static boolean isListening() {
        return mListeningInfo != null;
    }

    public static boolean isConnecting() {
        return mConnectingDevices.size() > 0;
    }

    public static boolean isConnecting(BluetoothDevice device) {
        return mConnectingDevices.containsKey(device);
    }

    public static void cancelConnecting(BluetoothDevice device) {
        if (mConnectingDevices.containsKey(device)) {
            ConnectTask task = mConnectingDevices.get(device);
            task.cancel(false);
            mConnectingDevices.remove(device);
            fireOnConnectionCancelledListeners(device);
        }
    }

    public static void cancelAllConnecting() {
        for (Map.Entry<BluetoothDevice, ConnectTask> entry : mConnectingDevices.entrySet()) {
            ConnectTask task = entry.getValue();
            task.cancel(false);
            mConnectingDevices.remove(entry.getKey());
            fireOnConnectionCancelledListeners(entry.getKey());
        }
    }

    public static boolean isConnected() {
        return mConnections.size() > 0;
    }

    public static boolean isConnected(BluetoothDevice device) {
        return getConnectionByDevice(device) != null;
    }

    public static boolean isSearching() {
        return isSearching;
    }

    public static void listen(final String name, final UUID uuid) {
        listen(new ListeningInfo(name, uuid));
    }

    public static void listen(ListeningInfo listeningInfo) {
        if (isListening())
            throw new IllegalStateException("Bluetooth is already listening");

        mListeningInfo = listeningInfo;
        stopSearching();
        listenTask = new ListenTask(mBluetoothAdapter, listeningInfo.getName(), listeningInfo.getUUID(), new OnDeviceConnectedListener() {
            @Override
            public void onDeviceConnected(BluetoothConnection connection) {
                mListeningInfo = null;
                listenTask = null;
                addConnection(connection);
            }
        });
        listenTask.execute();
    }

    public static void listen(final String name, final String uuid) {
        listen(new ListeningInfo(name, UUID.fromString(uuid)));
    }

    public static void stopListening() {
        if (isListening() && listenTask != null) {
            listenTask.cancel(false);
        }
    }

    public static void connect(final Context context, final BluetoothDevice device, final UUID uuid) {
        if (isConnecting(device)) {
            throw new IllegalStateException("Device is already connecting now");
        }

        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                BroadcastReceiver bondingReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        BluetoothDevice bondedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (bondedDevice.equals(device)) {
                            connect(context, device, uuid);
                            context.unregisterReceiver(this);
                        }
                    }
                };

                context.registerReceiver(bondingReceiver, filter);
                device.createBond();
                return;
            }
        }

        stopSearching();
        ConnectTask task = new ConnectTask(device, uuid, new OnDeviceConnectedListener() {
            @Override
            public void onDeviceConnected(BluetoothConnection connection) {
                mConnectingDevices.remove(device);
                addConnection(connection);
            }
        });
        mConnectingDevices.put(device, task);
        fireOnDeviceConnectingListeners(device);
        task.execute();
    }

    public static void closeAllConnections() {
        for (BluetoothConnection connection : mConnections) {
            Log.e("closeAllConnections", "Close All Connections");
            connection.close();
        }
    }

    public static void closeConnection(BluetoothDevice device) {
        BluetoothConnection connection = getConnectionByDevice(device);
        if (connection != null)
            connection.close();
    }

    public static void close(Context context) {
        stopSearching();
        stopListening();
        closeAllConnections();
        unregisterReceivers(context);
        removeAllListeners();
    }

    public void connect(Context context, final BluetoothDevice device, String uuid) {
        connect(context, device, UUID.fromString(uuid));
    }
}
