package com.example.golden.aviationrc.bluetooth;

/**
 * Created by Golden on 3/25/2018.
 */

public interface OnDiscoverabilityChangedListener {
    public void onBluetoothDiscoverabilityChanged(DiscoverabilityState isDiscoverable, BluetoothHelper helper);
}
