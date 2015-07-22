package co.andresp.kalmanorientation;

/**
 * Created by Andrés on 7/22/2015.
 */
public class Angle {
    public double angle = 0;

    public Angle(){
        this.angle = 0;
    }

    public Angle(double angle){
        this.angle = angle;
    }

    public double inDegress(){
        return angle * 180d / Math.PI;
    }

    /**
     * Finds the shortest diff between two angles around 0 or 180/-180
     * @param other the other angle to be subtracted
     * @return the difference between the angles
     */
    public double diff(Angle other){
        double result = angle - other.angle;
        if(Math.signum(angle) != Math.signum(other.angle)){
            double around180 = 360 - Math.abs(angle) - Math.abs(other.angle);

            if(around180 < result){
                double sign = 1;
                if(other.angle < angle) sign = -1;
                result = sign * around180;
            }
        }
        return result;
    }

    public static double mean(Iterable<Angle> angles){
        double sumPos = 0, sumNeg = 0;
        int countPos = 0, countNeg = 0;
        for(Angle a : angles){
            if(a.angle > 0){
                sumPos += a.angle;
                countPos++;
            } else {
                sumNeg -= a.angle;
                countNeg++;
            }
        }
        sumPos *= countPos;
        sumNeg *= countNeg;
        double mean = (sumPos + sumNeg)/ (double)(countPos + countNeg);
        if(sumNeg > sumPos) mean *= -1;
        return mean;
    }
}
