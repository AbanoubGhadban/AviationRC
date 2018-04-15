package com.example.golden.aviationrc;

import android.content.Context;

import com.example.golden.aviationrc.bluetooth.BluetoothConnection;
import com.example.golden.aviationrc.bluetooth.BluetoothHelper;

/**
 * Created by Golden on 3/27/2018.
 */

public class Commands {
    public static final String FORWARD_ID = "command_FORWARD";
    public static final String BACKWARD_ID = "command_BACKWARD";
    public static final String RIGHT_ID = "command_RIGHT";
    public static final String LEFT_ID = "command_LEFT";
    public static final String TOP_LEFT_ID = "command_TOP_LEFT";
    public static final String TOP_RIGHT_ID = "command_TOP_RIGHT";
    public static final String BACK_LEFT_ID = "command_ BACK_LEFT";
    public static final String BACK_RIGHT_ID = "command_BACK_RIGHT";
    public static final String STOP_ID = "command_STOP";
    public static final String TURN_ON_LINE_FOLLOWER_ID = "command_TURN_ON_LINE_FOLLOWER";
    public static final String TURN_OFF_LINE_FOLLOWER_ID = "command_TURN_OFF_LINE_FOLLOWER";


    private static char forward = 'F';
    private static char backward = 'B';
    private static char right = 'R';
    private static char left = 'L';
    private static char topRight = 'r';
    private static char topLeft = 'l';
    private static char backRight = 'q';
    private static char backLeft = 'w';
    private static char stop = 'S';
    private static char turnOnLineFollower = 'Y';
    private static char turnOffLineFollower = 'y';

    private static boolean isLineFollower;
    private static char changeSpeed = 'c';

    public static char getTurnOnLineFollower() {
        return turnOnLineFollower;
    }

    public static char getTurnOffLineFollower() {
        return turnOffLineFollower;
    }



    public static char getForward() {
        return forward;
    }

    public static char getBackward() {
        return backward;
    }

    public static char getRight() {
        return right;
    }

    public static char getLeft() {
        return left;
    }

    public static char getTopRight() {
        return topRight;
    }

    public static char getTopLeft() {
        return topLeft;
    }

    public static char getBackRight() {
        return backRight;
    }

    public static char getBackLeft() {
        return backLeft;
    }

    public static char getStop() {
        return stop;
    }

    public static boolean isLineFollower() {return isLineFollower;}

    public static void startLineFollower() {
        if (BluetoothHelper.isConnected()) {
            BluetoothConnection connection = BluetoothHelper.getConnection(0);
            connection.write(getTurnOnLineFollower());
            connection.flush();
            isLineFollower = true;
        }
    }

    public static void stopLineFollower() {
        if (BluetoothHelper.isConnected()) {
            BluetoothConnection connection = BluetoothHelper.getConnection(0);
            connection.write(getTurnOffLineFollower());
            connection.flush();
            isLineFollower = false;
        }
    }

    public static char getChangeSpeed() {
        return changeSpeed;
    }

    public static void loadCommands(Context context) {

    }

    public static void saveCommands(Context context) {

    }
}
