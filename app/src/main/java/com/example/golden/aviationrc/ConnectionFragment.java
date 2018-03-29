package com.example.golden.aviationrc;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.BluetoothHelper;
import com.example.golden.aviationrc.bluetooth.OnConnectionCancelledListener;
import com.example.golden.aviationrc.bluetooth.OnConnectionClosedListener;
import com.example.golden.aviationrc.bluetooth.OnDeviceConnectedListener;
import com.example.golden.aviationrc.bluetooth.OnDeviceConnectingListening;

import java.util.Set;

/**
 * Created by Golden on 3/26/2018.
 */

public class ConnectionFragment extends DialogFragment implements OnDeviceConnectedListener, OnConnectionClosedListener, OnDeviceConnectingListening, OnConnectionCancelledListener {
    Set<BluetoothDevice> mDevices;
    Context mContext;
    private TextView mConState;
    private Spinner mDevicesSpinner;
    private Button btnConnect;

    private View.OnClickListener scanListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, SearchActivity.class);
            startActivity(intent);
            Log.e("Exit", "Exit");
            getDialog().dismiss();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_connection, container, false);
        getDialog().setTitle("Connection Settings");

        mDevices = BluetoothHelper.getBonedDevices();
        DevicesSpinnerAdapter adapter = new DevicesSpinnerAdapter(mContext, mDevices);

        mDevicesSpinner = view.findViewById(R.id.device_spinner);
        mDevicesSpinner.setAdapter(adapter);
        mDevicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                displayDeviceState();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                displayDeviceState();
            }
        });

        BluetoothHelper.addOnDeviceConnectedListener(this);
        BluetoothHelper.addOnConnectionClosedListener(this);
        BluetoothHelper.addOnDeviceConnectingListener(this);
        BluetoothHelper.addOnConnectionCancelledListener(this);
        mConState = view.findViewById(R.id.device_state_tv);

        btnConnect = view.findViewById(R.id.btn_connect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothDevice device = (BluetoothDevice) mDevicesSpinner.getSelectedItem();

                if (device != null) {
                    if (BluetoothHelper.isConnecting(device))
                        BluetoothHelper.cancelConnecting(device);
                    else if (BluetoothHelper.isConnected(device))
                        BluetoothHelper.closeConnection(device);
                    else
                        BluetoothHelper.connect(mContext, device, Global.UUID_INSECURE);
                }
            }
        });

        Button btnScan = view.findViewById(R.id.btn_scan);
        ImageButton imgScan = view.findViewById(R.id.search_img);
        btnScan.setOnClickListener(scanListener);
        imgScan.setOnClickListener(scanListener);

        displayConnectedDevice();
        displayDeviceState();

        return view;
    }

    private void displayDeviceState(){
        BluetoothDevice device = (BluetoothDevice) mDevicesSpinner.getSelectedItem();
        if (device != null) {
            if (BluetoothHelper.isConnected(device)) {
                mConState.setText("Connected");
                mConState.setVisibility(View.VISIBLE);
                btnConnect.setText("Close");
            } else if (BluetoothHelper.isConnecting(device)) {
                mConState.setText("Connecting..");
                mConState.setVisibility(View.VISIBLE);
                btnConnect.setText("Cancel");
            } else {
                mConState.setVisibility(View.GONE);
                btnConnect.setText("Connect");
            }
        } else {
            mConState.setVisibility(View.GONE);
            btnConnect.setText("Connect");
        }
    }

    private void displayConnectedDevice(){
        if (BluetoothHelper.isConnected()) {
            BluetoothDevice connectedDevice = BluetoothHelper.getConnection(0).getRemoteDevice();
            mDevicesSpinner.setSelection(Global.getDeviceIndexFromSet(mDevices, connectedDevice));
        }
    }

    @Override
    public void onDeviceConnected(BluetoothConnection connection) {
        if (connection != null) {
            Toast.makeText(mContext, "Device Connected", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mContext, "Failed to Connect", Toast.LENGTH_LONG).show();
        }
        displayConnectedDevice();
        displayDeviceState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BluetoothHelper.removeAllListeners(this);
    }

    @Override
    public void onConnectionClosed(BluetoothDevice device) {
        displayDeviceState();
    }

    @Override
    public void onDeviceConnecting(BluetoothDevice device) {
        displayDeviceState();
    }

    @Override
    public void onConnectionCancelled(BluetoothDevice device) {
        displayDeviceState();
    }
}
