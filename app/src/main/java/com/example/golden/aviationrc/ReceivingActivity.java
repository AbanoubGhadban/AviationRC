package com.example.golden.aviationrc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.BluetoothHelper;

public class ReceivingActivity extends AppCompatActivity {
    ReceivingTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving);

        if (BluetoothHelper.isConnected()) {
            final TextView textView = findViewById(R.id.messages_tv);
            task = new ReceivingTask(BluetoothHelper.getConnection(0), new OnMessageReceivedListener() {
                @Override
                public void onMessageReceived(String message, BluetoothConnection connection, ReceivingTask task) {
                    textView.append(message + "\n");
                }
            });
            task.execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.cancel(false);
    }
}
