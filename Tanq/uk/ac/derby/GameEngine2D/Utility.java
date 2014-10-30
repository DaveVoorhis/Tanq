package uk.ac.derby.GameEngine2D;

/** Miscellaneous constants and functions. */
public class Utility {
	
	public final static float g	= -32.174f;		// acceleration due to gravity, ft/s^2
	public final static float rho = 0.0023769f;	// density of air at sea level, slugs/ft^3
	public final static float nearZero = 0.000000000000001f;		// float type tolerance 
    
    /** Delay. 
     *
     * @param milliseconds - number of milliseconds to delay
     */
    public static void delay(long milliseconds) {
    	try {
        	Thread.sleep(milliseconds);    		
    	} catch (InterruptedException ie) {}
    }
	
	/** Return polar orientation (in degrees from 0 - 360) from (x1, y1) to (x2, y2) */
	public final static double orientTo(double x1, double y1, double x2, double y2) {
        return (radiansToDegrees(Math.atan2(y2 - y1, x2 - x1)) + 720.0) % 360.0;
    }
	
	/** Given three Vector3D instances p, q, and r representing points on the Cartesian coordinate system,
	 * use the Law of Cosines to obtain the angle, in degrees, opposite line segment pq, 
	 * i.e., the angle at point r.
	 */
	public final static double getRelativeAngle(Vector3D p, Vector3D q, Vector3D r) {
		float a = Vector3D.subtract(p, q).getMagnitude();
		float b = Vector3D.subtract(p, r).getMagnitude();
		float c = Vector3D.subtract(r, q).getMagnitude();
		return radiansToDegrees(Math.acos((b * b + c * c - a * a) / (2 * b * c)));
	}
	
	public final static double degreesToRadians(double deg) {
		return deg * Math.PI / 180.0f;
	}

	public final static double radiansToDegrees(double rad) {	
		return rad * 180.0f / Math.PI;
	}	
}
