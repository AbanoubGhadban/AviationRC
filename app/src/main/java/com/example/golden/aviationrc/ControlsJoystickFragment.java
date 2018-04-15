package com.example.golden.aviationrc;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.BluetoothHelper;

/**
 * Created by Golden on 3/31/2018.
 */

public class ControlsJoystickFragment extends Fragment implements View.OnTouchListener {
    private FrameLayout frameLayout;
    private Button btnControl;
    private float dX, dY, width, height, xInitial, yInitial, maxLength;
    private char lastCommand;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_controls_joystick, container, false);
        btnControl = view.findViewById(R.id.btn_control);
        btnControl.setOnTouchListener(this);
        frameLayout = view.findViewById(R.id.frame_layout);
        return view;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float X = motionEvent.getRawX();
        float Y = motionEvent.getRawY();

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = X - view.getX();
                dY = Y - view.getY();
                width = frameLayout.getWidth();
                height = frameLayout.getHeight();
                xInitial = btnControl.getX();
                yInitial = btnControl.getY();

                float min = Math.min(height, width);
                maxLength = (float) (min*.4);
                lastCommand = '\0';
                break;
            case MotionEvent.ACTION_MOVE:
                setAppropriatePosition(X - dX, Y - dY, view);
                sendCommand(X - dX, Y - dY);
                break;
            case MotionEvent.ACTION_UP:
                view.setX(xInitial);
                view.setY(yInitial);
                lastCommand = '\0';
                sendCommand(Commands.getStop());
        }
        return false;
    }

    private void sendCommand(float X, float Y) {
        float angle = getAngleInDegree(X, Y);
        if (getLength(X, Y) >= maxLength*.5) {
            if (inRange(337.5f, 0, angle) || inRange(0, 22.5f, angle))
                sendCommand(Commands.getRight());
            else if (inRange(22.5f, 67.5f, angle))
                sendCommand(Commands.getTopRight());
            else if (inRange(67.5f, 112.5f, angle))
                sendCommand(Commands.getForward());
            else if (inRange(112.5f, 157.5f, angle))
                sendCommand(Commands.getTopLeft());
            else if (inRange(157.5f, 202.5f, angle))
                sendCommand(Commands.getLeft());
            else if (inRange(202.5f, 247.5f, angle))
                sendCommand(Commands.getBackLeft());
            else if (inRange(247.5f, 292.5f, angle))
                sendCommand(Commands.getBackward());
            else
                sendCommand(Commands.getBackRight());
        }
    }

    private boolean inRange(float min, float max, float value) {
        return (value >= min && value <= max);
    }

    private void sendCommand(char command) {
        if (lastCommand != command && BluetoothHelper.isConnected()) {
            BluetoothConnection connection = BluetoothHelper.getConnection(0);
            connection.write(command);
            connection.flush();
            lastCommand = command;
        }
    }

    private void setAppropriatePosition(float X, float Y, View view) {
        float len = getLength(X, Y);
        float angle = getAngle(X, Y);
        if (len <= maxLength) {
            view.setX(X);
            view.setY(Y);
        } else {
            view.setX((float) (xInitial + maxLength*Math.cos(angle)));
            view.setY((float) (yInitial - maxLength*Math.sin(angle)));
        }
    }

    private float getAngle(float X, float Y) {
        return (float) Math.atan2(yInitial - Y, X - xInitial);
    }

    private float getAngleInDegree(float X, float Y) {
        float angle = (float) (getAngle(X, Y)/Math.PI*180);
        if (angle < 0)
            angle = 360 + angle;
        return angle;
    }

    private float getLength(float X, float Y) {
        float xLen = X - xInitial;
        float yLen = Y - yInitial;
        return (float) Math.sqrt(Math.pow(xLen, 2) + Math.pow(yLen, 2));
    }
}
