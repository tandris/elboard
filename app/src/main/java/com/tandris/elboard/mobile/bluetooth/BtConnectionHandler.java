package com.tandris.elboard.mobile.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Bluetooth device handler which sends and retrieves data from the selected BT device.
 * <p/>
 * Created by Andras Toth on 2015. 10. 18..
 */
public class BtConnectionHandler {

    private static final String TAG = BtConnectionHandler.class.getName();
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String btDeviceAddress = null;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    private Callable<Void> connectionListener;

    public BtConnectionHandler() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * It selects a device by it's name.
     *
     * @param btDeviceName - the name of the BT device
     * @return - the handler
     * @throws Exception if no device found with the given name
     */
    public BtConnectionHandler selectDeviceByName(String btDeviceName) throws Exception {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals(btDeviceName)) {
                btDeviceAddress = device.getAddress();
                return this;
            }
        }
        throw new Exception("Device '" + btDeviceName + "' not found.");
    }

    /**
     * It connects to the selected BT device.
     *
     * @return the handler instance
     * @throws Exception
     */
    public BtConnectionHandler connect(Callable<Void> connectionListener) throws Exception {
        return connectToDevice(btDeviceAddress, connectionListener);
    }

    /**
     * It connects to the device with the given MAC address.
     *
     * @param address - the device's MAC address
     * @throws Exception if the connecting failed.
     */
    public BtConnectionHandler connectToDevice(final String address, Callable<Void> connectionListener) throws Exception {
        this.connectionListener = connectionListener;
        new Thread(new Runnable() {
            @Override
            public void run() {
                BluetoothDevice device = btAdapter.getRemoteDevice(address);

                try {
                    btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);

                    btAdapter.cancelDiscovery();

                    try {
                        // Establish the connection.  This will block until it connects.
                        btSocket.connect();
                    } catch (IOException e2) {
                        btSocket.close();

                        Log.e(TAG, "Unable to close socket during connection failure.", e2);
                        throw e2;
                    }

                    // Create a data stream so we can talk to server.
                    outStream = btSocket.getOutputStream();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to create socket to bluetooth device. { address = " + address + " }", e);
                    btSocket = null;
                    outStream = null;
                } finally {
                    try {
                        BtConnectionHandler.this.connectionListener.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return this;
    }

    /**
     * It sends the data to the selected device.
     *
     * @param message - the message to be sent
     * @throws Exception if the sending has failed
     */
    public void sendData(String message) throws Exception {
        if (outStream != null) {
            byte[] msgBuffer = message.getBytes();
            try {
                outStream.write(msgBuffer);
            } catch (IOException e) {
                String msg = "Bluetooth device output stream write error.";
                Log.e(TAG, msg, e);
                throw new Exception(msg);
            }
        } else {
            throw new Exception("No bluetooth device selected.");
        }
    }

    public void sendData(int value) throws Exception {
        if (outStream != null) {
            try {
                outStream.write(value);
            } catch (IOException e) {
                String msg = "Bluetooth device output stream write error.";
                Log.e(TAG, msg, e);
                throw new Exception(msg);
            }
        } else {
            throw new Exception("No bluetooth device selected.");
        }
    }

    /**
     * Disconnects the connected device.
     *
     * @return handler instance
     * @throws Exception if connection error
     */
    public BtConnectionHandler disconnect() throws Exception {
        if (btSocket != null) {
            btSocket.close();
            btSocket = null;
            BtConnectionHandler.this.connectionListener.call();
        }
        return this;
    }

    /**
     * Returns the current connection status.
     *
     * @return true if connected
     */
    public boolean isConnected() {
        return btSocket != null;
    }

    /**
     * Returns the currently connected device's name.
     *
     * @return the device name
     */
    public String getConnectedBtDeviceName() {
        if (isConnected()) {
            return btSocket.getRemoteDevice().getName();
        }
        return null;
    }
}
