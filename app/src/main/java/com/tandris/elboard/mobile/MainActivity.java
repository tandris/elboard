package com.tandris.elboard.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.tandris.elboard.mobile.bluetooth.ElBoardCommunicationHandler;

import java.util.concurrent.Callable;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 3;

    private Handler uiHandler;

    private ElBoardCommunicationHandler boardConnectionHandler;

    private static final String STATUS_TEMPLATE = "<table>" +
            "<tr><td>Device name: </td><td>%DEVICE_NAME%</td></tr>" +
            "<tr><td>Current speed:</td><td>%CURRENT_SPEED%%</td></tr>" +
            "</tr>" +
            "</table>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiHandler = new Handler();

        initPowerSwitch();
        initConnectionSwitch();
        initSpeedSeeker();

        boardConnectionHandler = new ElBoardCommunicationHandler();
        try {
            boardConnectionHandler.selectDeviceByName("BandiBot");
        } catch (Exception e) {
            errorExit("BT connection failed.", "BT connection failed.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent serverIntent = null;
        switch (id) {
            case R.id.secure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, BtDeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.action_settings:
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        try {
            boardConnectionHandler.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String address = data.getExtras().getString(BtDeviceListActivity.EXTRA_DEVICE_ADDRESS);
                        connectToDevice(address);
                    } catch (Exception e) {
                        errorExit("Connection error", e.getMessage());
                    }
                }
                break;
        }
    }

    private void errorExit(String title, String message) {
        Toast msg = Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_SHORT);
        msg.show();
        //finish();
    }

    private void updateConnectionStatus() {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                WebView statusView = (WebView) findViewById(R.id.status_view);

                String statusValue = STATUS_TEMPLATE.replaceAll("%DEVICE_NAME%", (boardConnectionHandler.isConnected() ? boardConnectionHandler.getConnectedBtDeviceName() : "no device"));
                statusValue = statusValue.replaceAll("%CURRENT_SPEED%", String.valueOf(boardConnectionHandler.getCurrentSpeedPercentage()));
                statusView.loadData(statusValue, "text/html", null);

                ((Switch) findViewById(R.id.connectionStatus)).setChecked(boardConnectionHandler.isConnected());
                ((Switch) findViewById(R.id.start_motor)).setEnabled(boardConnectionHandler.isPowerOn());
            }
        });
    }

    public void initPowerSwitch() {
        ((Switch) findViewById(R.id.start_motor)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if(isChecked) {
                        boardConnectionHandler.startMotor();
                    } else {
                        boardConnectionHandler.stopMotor();
                    }
                } catch (Exception e) {
                    errorExit("Communication error", e.getMessage());
                }
            }
        });
    }

    public void initConnectionSwitch() {
        ((Switch) findViewById(R.id.connectionStatus)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (!isChecked && boardConnectionHandler.isConnected()) {
                        boardConnectionHandler.disconnect();
                    } else if (isChecked && !boardConnectionHandler.isConnected()) {
                        connectToDevice(null);
                    }
                } catch (Exception e) {
                    errorExit("Communication error", e.getMessage());
                }
            }
        });
    }

    public void initSpeedSeeker() {
        ((SeekBar) findViewById(R.id.speepSeek)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    float ratio = (100f - ElBoardCommunicationHandler.NEUTRAL_VALUE) / 100f;
                    boardConnectionHandler.setCurrentSpeed(((int)(progress * ratio)) + ElBoardCommunicationHandler.NEUTRAL_VALUE);
                    updateConnectionStatus();
                } catch (Exception e) {
                    errorExit("Speed set error", e.getMessage());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void connectToDevice(String address) throws Exception {
        if (address == null) {
            boardConnectionHandler.connect(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    updateConnectionStatus();
                    return null;
                }
            });
        } else {
            boardConnectionHandler.connectToDevice(address, new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    updateConnectionStatus();
                    return null;
                }
            });
        }
    }

    /*public void connectMetaWear(View view) {
        if (metaUtil != null) {
            metaUtil.connect((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) findViewById(R.id.boardStatusTest)).setText(metaUtil.getState().name());
                            if (metaUtil.getState().equals(MetaWearUtil.STATE.CONNECTED)) {
                                ((TextView) findViewById(R.id.boardStatusTest)).setTextColor(Color.GREEN);
                                findViewById(R.id.statusBtn).setEnabled(true);
                                initView();
                            } else {
                                ((TextView) findViewById(R.id.boardStatusTest)).setTextColor(Color.RED);
                                findViewById(R.id.statusBtn).setEnabled(false);
                            }
                        }
                    });
                    return null;
                }
            });
        }
    }

    public void statusMetaWear(View view) {
        if (metaUtil.getState().equals(MetaWearUtil.STATE.CONNECTED)) {
            initView();
        }
    }

    private void initView() {
        metaUtil.getBoard().readBatteryLevel().onComplete(new AsyncOperation.CompletionHandler<Byte>() {
            @Override
            public void success(final Byte result) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((ProgressBar) findViewById(R.id.batteryLevel)).setProgress(result.intValue());
                    }
                });
            }

            @Override
            public void failure(Throwable error) {
                super.failure(error);
            }
        });
    }

    public void startMotor(View view) {
        setServo();
    }

    private void setServo() {
        try {
            float value = (int) getServoValue();
            metaUtil.getBoard().getModule(Haptic.class).startMotor(100, (short) value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float getServoValue() {
        long max = 2400;
        long min = 544;
        float value = (float) (((SeekBar) findViewById(R.id.seekBar)).getProgress());
        value /= 180;
        value *= 100;
        value = ((int) (value / 10)) * 10;
        return value;
    }*/

}
