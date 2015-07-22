package co.andresp.kalmanorientation;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;

public class PlotActivity extends AppCompatActivity {

    private ArrayList<float[]> mRotVecSamples;
    private ArrayList<float[]> mGyroSamples;
    private ArrayList<Float> orientations;
    private ArrayList<Float> angularAccel;
    private XYPlot mPlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        Intent passedData = getIntent();
        mRotVecSamples = (ArrayList<float[]>) passedData.getSerializableExtra(MainActivity.ROT_VEC_DATA);
        mGyroSamples = (ArrayList<float[]>) passedData.getSerializableExtra(MainActivity.GYRO_DATA);
        convertToAngularAcceleration();
        convertToOrientation();
        XYSeries series = new SimpleXYSeries(orientations, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Orientation X Axis");
        mPlot = (XYPlot) findViewById(R.id.plot);
        LineAndPointFormatter format = new LineAndPointFormatter(Color.RED, null, null, null);
        format.getLinePaint().setStrokeWidth(10);
        mPlot.addSeries(series, format);
        mPlot.setDomainBoundaries(0, orientations.size()-1, BoundaryMode.FIXED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_plot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_goback) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void convertToOrientation(){
        orientations = new ArrayList<>();
        for(float[] sampleValues : mRotVecSamples){
            float[] rotMat = new float[16];
            float[] result = new float[3];
            SensorManager.getRotationMatrixFromVector(rotMat, sampleValues);
            SensorManager.getOrientation(rotMat, result);
            orientations.add((float) (result[0] * 180 / Math.PI));
        }
    }

    private void convertToAngularAcceleration(){
        angularAccel = new ArrayList<>();
        for(float[] sample : mGyroSamples){
            angularAccel.add((float)(sample[0]*180/Math.PI));
        }
    }
}
