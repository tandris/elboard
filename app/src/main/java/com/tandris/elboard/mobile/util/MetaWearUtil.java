package com.tandris.elboard.mobile.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.util.Log;

import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.module.Gpio;

import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * Created by Bandi on 2015. 09. 19..
 */
public class MetaWearUtil {

    public enum STATE {
        CONNECTED,
        DISCONNECTED,
        FAILED
    }

    private static final String MAC_ADDRESS = "E4:24:D2:5C:F1:5A";

    private MetaWearBleService.LocalBinder binder;
    private MetaWearBoard board;
    private STATE state = STATE.DISCONNECTED;

    public MetaWearUtil(MetaWearBleService.LocalBinder binder) {
        this.binder = binder;
    }

    public void connect(BluetoothManager btManager, final Callable<Void> connectionListener) {
        if (this.state != STATE.CONNECTED) {
            final BluetoothDevice remoteDevice = btManager.getAdapter().getRemoteDevice(MAC_ADDRESS);
            this.board = binder.getMetaWearBoard(remoteDevice);

            MetaWearBoard.ConnectionStateHandler stateHandler = new MetaWearBoard.ConnectionStateHandler() {
                @Override
                public void connected() {
                    state = STATE.CONNECTED;
                    try {
                        connectionListener.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void disconnected() {
                    state = STATE.DISCONNECTED;
                    try {
                        connectionListener.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(int status, Throwable error) {
                    state = STATE.FAILED;
                    try {
                        connectionListener.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            board.setConnectionStateHandler(stateHandler);
            board.connect();
        }
    }

    public MetaWearBoard getBoard() {
        return board;
    }

    public STATE getState() {
        return state;
    }

}
