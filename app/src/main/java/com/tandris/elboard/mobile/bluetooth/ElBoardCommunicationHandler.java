package com.tandris.elboard.mobile.bluetooth;

import java.util.concurrent.Callable;

/**
 * Created by Bandi on 2015. 10. 24..
 */
public class ElBoardCommunicationHandler extends BtConnectionHandler {
    public static final int NEUTRAL_VALUE = 65;
    public static final int START_VALUE = 1;
    public static final int STOP_VALUE = 0;

    private int currentSpeed = NEUTRAL_VALUE;

    public ElBoardCommunicationHandler() {
        super();
    }

    public BtConnectionHandler connectToDevice(final String address, final Callable<Void> connectionListener) throws Exception {
        currentSpeed = NEUTRAL_VALUE;
        BtConnectionHandler btConnectionHandler = super.connectToDevice(address, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                sendSetSpeedCommand();
                connectionListener.call();
                return null;
            }
        });
        return btConnectionHandler;
    }

    protected void sendSetSpeedCommand() throws Exception {
        sendData(currentSpeed);
    }

    /**
     * Returns the actual speed.
     *
     * @return the speed value
     */
    public int getCurrentSpeed() {
        return currentSpeed;
    }

    public int getCurrentSpeedPercentage() {
        float percentage = (currentSpeed - NEUTRAL_VALUE) / ((100f - NEUTRAL_VALUE) / 100f);
        return (int)percentage;
    }

    /**
     * It set the neutral value for the motor, to start to operate.
     * @throws Exception
     */
    public void startMotor() throws Exception {
        setCurrentSpeed(START_VALUE);
        setCurrentSpeed(NEUTRAL_VALUE);
    }

    public void stopMotor() throws Exception {
        setCurrentSpeed(STOP_VALUE);
    }

    public boolean isPowerOn() {
        return getCurrentSpeed() > 0;
    }

    /**
     * It set's the current speed on the board.
     *
     * @param currentSpeed the new speed
     * @throws Exception
     */
    public void setCurrentSpeed(int currentSpeed) throws Exception {
        if (currentSpeed > 100 || currentSpeed < 0) {
            throw new Exception("Invalid speed value.");
        }
        this.currentSpeed = currentSpeed;
        sendSetSpeedCommand();
    }
}
