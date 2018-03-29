package com.example.golden.aviationrc;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.BluetoothHelper;
import com.example.golden.aviationrc.bluetooth.BluetoothState;
import com.example.golden.aviationrc.bluetooth.OnConnectionCancelledListener;
import com.example.golden.aviationrc.bluetooth.OnConnectionClosedListener;
import com.example.golden.aviationrc.bluetooth.OnDeviceConnectedListener;
import com.example.golden.aviationrc.bluetooth.OnDeviceConnectingListening;
import com.example.golden.aviationrc.bluetooth.OnStateChangedListener;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements OnStateChangedListener, OnDeviceConnectingListening, OnDeviceConnectedListener, OnConnectionClosedListener, View.OnTouchListener, OnConnectionCancelledListener {
    private ListView mProblemsLV;
    private Button btnConnect;
    private Button btnDisconnect;

    private static final int COMMANDS_HISTORY_SIZE = 5;
    private final LinkedList<Character> mCommandsHistory = new LinkedList<>();
    ReceivingTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        return;

//        BluetoothHelper.prepareBluetooth(getApplicationContext());
//
//        mProblemsLV = new ListView(getApplicationContext());
//        ListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        mProblemsLV.setLayoutParams(lp);
//        prepareActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adjustButtonsBackground();
    }

    private boolean checkIfPermissionsGranted() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    LinkedList<ProblemsListViewAdapter.ProblemHolder> getProblems(){
        LinkedList<ProblemsListViewAdapter.ProblemHolder> problems = new LinkedList<>();
        if (!checkIfPermissionsGranted()){
            ProblemsListViewAdapter.ProblemHolder problem = new ProblemsListViewAdapter.ProblemHolder();
            problem.setProblemText(getString(R.string.problem_need_permission));
            problem.setSolveText(getString(R.string.grant_permissions));
            problem.setSolveListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                    }
                }
            });
            problems.add(problem);
        }

        if (!BluetoothHelper.isEnabled()) {
            ProblemsListViewAdapter.ProblemHolder problem = new ProblemsListViewAdapter.ProblemHolder();
            problem.setProblemText(getString(R.string.problem_bt_off));
            problem.setSolveText(getString(R.string.turn_on));
            problem.setSolveListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BluetoothHelper.enableBluetooth(getApplicationContext());
                }
            });
            problems.add(problem);
        }
        return problems;
    }

    private void displayProblems(LinkedList<ProblemsListViewAdapter.ProblemHolder> problems) {
        ProblemsListViewAdapter adapter = new ProblemsListViewAdapter(getApplicationContext(), problems);
        mProblemsLV.setAdapter(adapter);
        setContentView(mProblemsLV);
    }

    void prepareActivity() {
        BluetoothHelper.addOnStateChangedListener(this);
        BluetoothHelper.addOnDeviceConnectingListener(this);
        BluetoothHelper.addOnDeviceConnectedListener(this);
        BluetoothHelper.addOnConnectionClosedListener(this);
        BluetoothHelper.addOnConnectionCancelledListener(this);
        LinkedList<ProblemsListViewAdapter.ProblemHolder> problems = getProblems();

        if (problems.size() > 0)
            displayProblems(problems);
        else {
            setContentView(R.layout.activity_main);

            btnConnect = findViewById(R.id.btn_connection);
            btnDisconnect = findViewById(R.id.btn_disconnect);
            findViewById(R.id.btn_forward).setOnTouchListener(this);
            findViewById(R.id.btn_backward).setOnTouchListener(this);
            findViewById(R.id.btn_left).setOnTouchListener(this);
            findViewById(R.id.btn_right).setOnTouchListener(this);
            findViewById(R.id.btn_top_left).setOnTouchListener(this);
            findViewById(R.id.btn_top_right).setOnTouchListener(this);
            findViewById(R.id.btn_back_left).setOnTouchListener(this);
            findViewById(R.id.btn_back_right).setOnTouchListener(this);

            adjustButtonsBackground();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        prepareActivity();
    }

    public void btn_connection_onClick(View view) {
        ConnectionFragment fragment = new ConnectionFragment();
        fragment.show(getSupportFragmentManager(), "Dialog Fragment");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothHelper.close(getApplicationContext());
        task.cancel(false);
    }

    @Override
    public void onBluetoothStateChanged(BluetoothState state, BluetoothState prevState) {
        prepareActivity();
    }


    @Override
    public void onDeviceConnecting(BluetoothDevice device) {
        adjustButtonsBackground();
    }

    @Override
    public void onDeviceConnected(final BluetoothConnection connection) {
        adjustButtonsBackground();

//        final TextView textView = findViewById(R.id.messages_tv);
//        task = new ReceivingTask(connection, new OnMessageReceivedListener() {
//            @Override
//            public void onMessageReceived(String message, BluetoothConnection connection, ReceivingTask task) {
//                textView.append(message + "--");
//            }
//        });
//        task.execute();
    }

    @Override
    public void onConnectionClosed(BluetoothDevice device) {
        adjustButtonsBackground();
    }

    private void adjustButtonsBackground(){
        int backgroundRes;
        if (BluetoothHelper.isConnected())
            backgroundRes = R.drawable.background_connected;
        else if (BluetoothHelper.isConnecting())
            backgroundRes = R.drawable.background_connecting;
        else
            backgroundRes = R.drawable.background_disconnected;

        if (btnConnect != null)
            btnConnect.setBackgroundResource(backgroundRes);

        if (btnDisconnect != null) {
            if (BluetoothHelper.isConnected())
                btnDisconnect.setEnabled(true);
            else
                btnDisconnect.setEnabled(false);
        }
    }

    private void sendCommand(char command) {
        BluetoothConnection connection = BluetoothHelper.getConnection(0);
        connection.write(new char[] {command});
        connection.flush();

        if (mCommandsHistory.size() == COMMANDS_HISTORY_SIZE)
            mCommandsHistory.removeFirst();

        mCommandsHistory.add(command);
    }

    private void releaseCommand(char command){
        BluetoothConnection connection = BluetoothHelper.getConnection(0);
        mCommandsHistory.remove((Object)command);
        if (mCommandsHistory.size() > 0)
            connection.write(new char[] {mCommandsHistory.getLast()});
        else
            connection.write(new char[] {Commands.getStop()});
        connection.flush();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (BluetoothHelper.isConnected()){
            char command = '\0';
            switch (view.getId()) {
                case R.id.btn_forward:
                    command = Commands.getForward();
                    break;
                case R.id.btn_backward:
                    command = Commands.getBackward();
                    break;
                case R.id.btn_right:
                    command = Commands.getRight();
                    break;
                case R.id.btn_left:
                    command = Commands.getLeft();
                    break;
                case R.id.btn_top_right:
                    command = Commands.getTopRight();
                    break;
                case R.id.btn_top_left:
                    command = Commands.getTopLeft();
                    break;
                case R.id.btn_back_right:
                    command = Commands.getBackRight();
                    break;
                case R.id.btn_back_left:
                    command = Commands.getBackLeft();
                    break;
            }

            if (command != '\0') {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    sendCommand(command);
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    releaseCommand(command);
            }
        } else {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                Toast.makeText(this, getString(R.string.device_not_connected), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void btn_disconnect_onClick(View view) {
        if (BluetoothHelper.isConnected())
            BluetoothHelper.getConnection(0).close();
    }

    @Override
    public void onConnectionCancelled(BluetoothDevice device) {
        adjustButtonsBackground();
    }

//    public void btn_messages_onClick(View view) {
//        Intent intent = new Intent(this, ReceivingActivity.class);
//        startActivity(intent);
//    }
}
