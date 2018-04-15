package com.example.golden.aviationrc;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.BluetoothHelper;

import java.util.LinkedList;

/**
 * Created by Golden on 3/30/2018.
 */

public class ControlButtonsFragment extends Fragment implements View.OnTouchListener {
    private Context mContext;
    private static final int COMMANDS_HISTORY_SIZE = 5;
    private final LinkedList<Character> mCommandsHistory = new LinkedList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controls_buttons, container, false);
        view.findViewById(R.id.btn_forward).setOnTouchListener(this);
        view.findViewById(R.id.btn_backward).setOnTouchListener(this);
        view.findViewById(R.id.btn_left).setOnTouchListener(this);
        view.findViewById(R.id.btn_right).setOnTouchListener(this);
        view.findViewById(R.id.btn_top_left).setOnTouchListener(this);
        view.findViewById(R.id.btn_top_right).setOnTouchListener(this);
        view.findViewById(R.id.btn_back_left).setOnTouchListener(this);
        view.findViewById(R.id.btn_back_right).setOnTouchListener(this);
        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (BluetoothHelper.isConnected()) {
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
        }
        return false;
    }

    private void sendCommand(char command) {
        BluetoothConnection connection = BluetoothHelper.getConnection(0);
        connection.write(new char[]{command});
        connection.flush();

        if (mCommandsHistory.size() == COMMANDS_HISTORY_SIZE)
            mCommandsHistory.removeFirst();

        mCommandsHistory.add(command);
    }

    private void releaseCommand(char command) {
        BluetoothConnection connection = BluetoothHelper.getConnection(0);
        mCommandsHistory.remove((Object) command);
        if (mCommandsHistory.size() > 0)
            connection.write(new char[]{mCommandsHistory.getLast()});
        else
            connection.write(new char[]{Commands.getStop()});
        connection.flush();
    }
}
