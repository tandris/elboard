package metawear.tandris.com.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.module.Haptic;
import com.mbientlab.metawear.module.I2C;

import java.util.concurrent.Callable;

import metawear.tandris.com.myapplication.util.MetaWearUtil;

public class MainActivity extends Activity implements ServiceConnection {

    private MetaWearUtil metaUtil;
    private Handler uiHandler;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        metaUtil = new MetaWearUtil(((MetaWearBleService.LocalBinder) service));
        ((Button) findViewById(R.id.connectBtn)).setEnabled(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        metaUtil = null;
        ((Button) findViewById(R.id.connectBtn)).setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uiHandler = new Handler();
        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class), this, Context.BIND_AUTO_CREATE);

        ((SeekBar)findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) findViewById(R.id.seekValueTest)).setText(String.valueOf(getServoValue()));
                setServo();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectMetaWear(View view) {
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
            float value = (int)getServoValue();
            metaUtil.getBoard().getModule(Haptic.class).startMotor(100, (short)value);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private float getServoValue() {
        long max = 2400;
        long min = 544;
        float value = (float)(((SeekBar)findViewById(R.id.seekBar)).getProgress());
        value /= 180;
        value *= 100;
        value = ((int)(value / 10)) * 10;
        return value;
    }
}
