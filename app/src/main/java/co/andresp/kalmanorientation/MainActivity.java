package co.andresp.kalmanorientation;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    //Interface Items
    private TextView mXOrientation, mYOrientation, mZOrientation;
    private TextView mXGyroscope, mYGyroscope, mZGyroscope;
    private Button mStartStopButton;

    //Sensors
    private SensorManager mSensorManager;
    private ArrayList<Sensor> mSensors = new ArrayList<>();
    private ArrayList<float[]> mRotVecSamples = new ArrayList<>();
    private ArrayList<float[]> mGyroSamples = new ArrayList<>();
    private float[] mLastRotVecSample  = new float[3];
    private float[] mLastGyroSample  = new float[3];
    public final static String ROT_VEC_DATA = "rotVecData";
    public final static String GYRO_DATA = "gyroData";

    //Threads
    private ScheduledThreadPoolExecutor mStpe;
    private int refreshCounter = 50;
    private  Thread mCollectorThread = new Thread(new Runnable() {
        @Override
        public void run() {
            if(mLastRotVecSample[0] != 0f || mLastRotVecSample[1] != 0f || mLastRotVecSample[2] != 0f) {
                mRotVecSamples.add(mLastRotVecSample.clone());
                mGyroSamples.add(mLastGyroSample.clone());
                runOnUiThread(mUpdateValues);
            }
        }
    });
    private Runnable mUpdateValues = new Runnable() {
        @Override
        public void run() {
            mXOrientation.setText(""+mLastRotVecSample[0]);
            mYOrientation.setText(""+mLastRotVecSample[1]);
            mZOrientation.setText(""+mLastRotVecSample[2]);
            mXGyroscope.setText(""+mLastGyroSample[0]);
            mYGyroscope.setText(""+mLastGyroSample[1]);
            mZGyroscope.setText(""+mLastGyroSample[2]);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find interface elements
        mXOrientation = (TextView) findViewById(R.id.orientation_x_value);
        mYOrientation = (TextView) findViewById(R.id.orientation_y_value);
        mZOrientation = (TextView) findViewById(R.id.orientation_z_value);
        mXGyroscope = (TextView) findViewById(R.id.gyroscope_x_value);
        mYGyroscope = (TextView) findViewById(R.id.gyroscope_y_value);
        mZGyroscope = (TextView) findViewById(R.id.gyroscope_z_value);
        mStartStopButton = (Button) findViewById(R.id.start_stop_button);

        //SensorManager and Sensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
        mSensors.add(mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));

        mStartStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStartStopButton.getText().equals("Start")){ //Is gonna start
                    cleanEverything();
                    registerSensors();
                    mStpe = new ScheduledThreadPoolExecutor(5);
                    mStpe.scheduleAtFixedRate(mCollectorThread, 20, 20, TimeUnit.MILLISECONDS);
                    mStartStopButton.setText("Stop");
                } else { //Is going to stop
                    mStpe.shutdown();
                    unregisterSensors();
                    mStartStopButton.setText("Start");
                    startPlotActivity();
                }
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()){
            case Sensor.TYPE_ROTATION_VECTOR:
                mLastRotVecSample = event.values;
                break;
            case Sensor.TYPE_GYROSCOPE:
                mLastGyroSample = event.values;
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void registerSensors(){
        for(Sensor s : mSensors){
            mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    private void unregisterSensors(){
        for(Sensor s : mSensors){
            mSensorManager.unregisterListener(this, s);
        }
    }

    private void cleanEverything(){
        mRotVecSamples.clear();
        mGyroSamples.clear();
        mLastRotVecSample = new float[3];
        mLastGyroSample = new float[3];
    }

    private void startPlotActivity(){
        Intent plotActivity = new Intent(this, PlotActivity.class);
        plotActivity.putExtra(ROT_VEC_DATA, mRotVecSamples);
        plotActivity.putExtra(GYRO_DATA, mGyroSamples);
        startActivity(plotActivity);
    }
}
