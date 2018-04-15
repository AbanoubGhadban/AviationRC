package com.example.golden.aviationrc;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;
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

import javax.microedition.khronos.opengles.GL;

public class MainActivity extends AppCompatActivity implements OnStateChangedListener, OnDeviceConnectingListening, OnDeviceConnectedListener, OnConnectionClosedListener, OnConnectionCancelledListener, CompoundButton.OnCheckedChangeListener {
    private ListView mProblemsLV;
    private Button btnConnect;
    private Button btnDisconnect;
    private Switch mLineFollowerSwitch;
    private TextView mErrorTV;
    private ControlsJoystickFragment controlsJoystickFragment;
    private ControlButtonsFragment controlButtonsFragment;
    private static Fragment currentFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Global.context = getApplicationContext();

        BluetoothHelper.prepareBluetooth(getApplicationContext());

        mProblemsLV = new ListView(getApplicationContext());
        ListView.LayoutParams lp = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mProblemsLV.setLayoutParams(lp);
        prepareActivity();
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

    LinkedList<ProblemsListViewAdapter.ProblemHolder> getProblems() {
        LinkedList<ProblemsListViewAdapter.ProblemHolder> problems = new LinkedList<>();
        if (!checkIfPermissionsGranted()) {
            ProblemsListViewAdapter.ProblemHolder problem = new ProblemsListViewAdapter.ProblemHolder();
            problem.setProblemText(getString(R.string.problem_need_permission));
            problem.setSolveText(getString(R.string.grant_permissions));
            problem.setSolveListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
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
            mLineFollowerSwitch = findViewById(R.id.line_follower_switch);
            mErrorTV = findViewById(R.id.error_tv);

            mLineFollowerSwitch.setOnCheckedChangeListener(this);

            controlButtonsFragment = new ControlButtonsFragment();
            controlsJoystickFragment = new ControlsJoystickFragment();
            if (currentFragment == null)
                currentFragment = controlButtonsFragment;

            try {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_frame_layout, currentFragment).commitAllowingStateLoss();
            } catch (IllegalStateException ex) {}

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
        if (isFinishing())
            BluetoothHelper.close(getApplicationContext());
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
        Global.lastDisconnectedDevice = device;
        adjustButtonsBackground();
    }

    private void adjustButtonsBackground() {
        int backgroundRes;
        if (BluetoothHelper.isConnected())
            backgroundRes = R.mipmap.background_connection;
        else if (BluetoothHelper.isConnecting())
            backgroundRes = R.mipmap.background_disconnected;
        else
            backgroundRes = R.mipmap.background_disconnected;

        if (btnConnect != null)
            btnConnect.setBackgroundResource(backgroundRes);

        if (btnDisconnect != null) {
            if (BluetoothHelper.isConnected())
                btnDisconnect.setEnabled(true);
            else
                btnDisconnect.setEnabled(false);
        }

        if (mErrorTV != null) {
            if (BluetoothHelper.isConnected()){
                mErrorTV.setVisibility(View.GONE);
                mLineFollowerSwitch.setVisibility(View.VISIBLE);
            } else {
                mLineFollowerSwitch.setVisibility(View.GONE);
                mErrorTV.setVisibility(View.VISIBLE);
            }
        }
    }

    public void btn_disconnect_onClick(View view) {
        if (BluetoothHelper.isConnected())
            BluetoothHelper.getConnection(0).close();
    }

    @Override
    public void onConnectionCancelled(BluetoothDevice device) {
        adjustButtonsBackground();
    }

    //When Line Follower Switch checked or unchecked
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b)
            Commands.startLineFollower();
        else
            Commands.stopLineFollower();
    }

    public void btn_reconnect_onClick(View view) {
        if (!BluetoothHelper.isConnected() && Global.lastDisconnectedDevice != null)
            BluetoothHelper.connect(getApplicationContext(), Global.lastDisconnectedDevice, Global.UUID_INSECURE);
    }

    public void btn_controls_onClick(View view) {
        if (currentFragment instanceof ControlButtonsFragment)
            currentFragment = controlsJoystickFragment;
        else
            currentFragment = controlButtonsFragment;

        try {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder_frame_layout, currentFragment).commitAllowingStateLoss();
        } catch (IllegalStateException ex) {}
    }
}
