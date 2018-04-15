package com.example.golden.aviationrc;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.BluetoothHelper;
import com.example.golden.aviationrc.bluetooth.OnConnectionClosedListener;
import com.example.golden.aviationrc.bluetooth.OnDeviceConnectedListener;
import com.example.golden.aviationrc.bluetooth.OnDeviceFoundListener;
import com.example.golden.aviationrc.bluetooth.OnSearchingFinishedListener;
import com.example.golden.aviationrc.bluetooth.OnSearchingStartedListener;

import java.util.HashSet;

public class SearchActivity extends AppCompatActivity implements OnDeviceConnectedListener, OnConnectionClosedListener, OnSearchingStartedListener, OnSearchingFinishedListener, OnDeviceFoundListener, AdapterView.OnItemClickListener {
    private ProgressBar mSearchingPB;
    private Button mSearch_btn;
    private Button mCancel_btn;
    private ListView mDevicesLV;
    private HashSet<BluetoothDevice> mDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchingPB = findViewById(R.id.searching_pb);
        mSearch_btn = findViewById(R.id.btn_search);
        mCancel_btn = findViewById(R.id.btn_cancel);
        mDevicesLV = findViewById(R.id.devices_lv);

        mDevices = new HashSet<>();
        DevicesListViewAdapter devicesListViewAdapter = new DevicesListViewAdapter
                (getApplicationContext(), mDevices);
        mDevicesLV.setAdapter(devicesListViewAdapter);

        BluetoothHelper.addOnSearchingStartedListener(this);
        BluetoothHelper.addOnSearchingFinishedListener(this);
        BluetoothHelper.addOnDeviceFoundListener(this);

        mDevicesLV.setOnItemClickListener(this);
        BluetoothHelper.addOnDeviceConnectedListener(this);
        BluetoothHelper.addOnConnectionClosedListener(this);

        if (BluetoothHelper.isSearching())
            BluetoothHelper.startSearching();
        BluetoothHelper.startSearching();
    }

    private void refreshListView() {
        mDevicesLV.setAdapter(new DevicesListViewAdapter(getApplicationContext(), mDevices));
    }

    public void btn_search_onClick(View view) {
        if (BluetoothHelper.isSearching()) {
            BluetoothHelper.stopSearching();
        } else {
            BluetoothHelper.startSearching();
        }
    }

    public void btn_cancel_onClick(View view) {
        finish();
    }

    @Override
    public void onDeviceConnected(BluetoothConnection connection) {
        refreshListView();
    }

    @Override
    public void onConnectionClosed(BluetoothDevice device) {
        refreshListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothHelper.removeAllListeners(this);
    }

    @Override
    public void onSearchingStarted() {
        mDevices = new HashSet<>();
        refreshListView();
        mSearchingPB.setVisibility(View.VISIBLE);
        mSearch_btn.setText("Stop");
    }

    @Override
    public void onStopSearching() {
        mSearchingPB.setVisibility(View.GONE);
        mSearch_btn.setText("Search");
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {
        mDevices.add(device);
        refreshListView();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        BluetoothDevice device = Global.getDeviceFromSet(mDevices, i);
        if (BluetoothHelper.isConnecting(device))
            BluetoothHelper.cancelConnecting(device);
        else
            BluetoothHelper.connect(this, device, Global.UUID_INSECURE);
        refreshListView();
    }
}
