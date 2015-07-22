package co.andresp.kalmanorientation;

import java.util.ArrayList;

/**
 * Created by Andrés on 7/22/2015.
 */
public class KalmanOrientation {
    private final static double SAMPLING_RATE = 20; //20 Milliseconds between samples = 50Hz

    private final static double DT = SAMPLING_RATE/1000d;

    private ArrayList<Float> mOrientation;
    private ArrayList<Float> mAccel;

    private float eX, eZ;

    public ArrayList<Float> filtered;


    public KalmanOrientation(ArrayList<Float> orientation, ArrayList<Float> angularAccel){
        mOrientation = orientation;
        mAccel = angularAccel;
        eX = getVariance(orientation);
        eZ = (float)Math.sqrt(eX);

        filtered = new ArrayList<>();
        filtered.add(orientation.get(0));
    }
    /*
    This is like this in case that the values are around 180/-180 because the values could cancel
     each other, given the circular nature of the orientation at those points.
     */
    public float getMean(ArrayList<Float> values){
        float sumPos = 0;
        int countPos = 0;
        float sumNeg = 0;
        int countNeg = 0;
        for(float v : values){
            if(v > 0){
                sumPos += v;
                countPos++;
            }
            else{
                sumNeg -= v;
                countNeg++;
            }
        }
        sumPos *= countPos;
        sumNeg *= countNeg;
        float mean = (sumPos + sumNeg)/ values.size();
        if (sumNeg > sumPos) mean *= -1;
        return mean;
    }

    public float getVariance(ArrayList<Float> values){
        float mean = getMean(values);
        float sum = 0;
        for(float v : values){
            sum += Math.pow((v - mean), 2);
        }
        return sum/(float)values.size();
    }

}
