package uk.ac.derby.GameEngine2D;

public class Vector3D {
	private float x;
	private float y;
	private float z;

	public Vector3D() {
		reset();
	}
	
	public Vector3D(float xi, float yi, float zi) {
		x = xi;
		y = yi;
		z = zi;
	}

	public Vector3D(Vector3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}
	
	public void reset() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public void setX(float xi) {
		x = xi;
	}
	
	public float getX() {
		return x;
	}

	public void setY(float yi) {
		y = yi;
	}
	
	public float getY() {
		return y;
	}

	public void setZ(float zi) {
		z = zi;
	}
	
	public float getZ() {
		return z;
	}
	
	public final float getMagnitude() {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public final void normalise() {
		float m = getMagnitude();
		if (m <= Utility.nearZero)
			m = 1;
		x /= m;
		y /= m;
		z /= m;
		if (Math.abs(x) < Utility.nearZero)
			x = 0.0f;
		if (Math.abs(y) < Utility.nearZero)
			y = 0.0f;
		if (Math.abs(z) < Utility.nearZero)
			z = 0.0f;
	}
	
	public final void reverse() {
		x = -x;
		y = -y;
		z = -y;
	}

	public final Vector3D add(Vector3D u) {
		x += u.x;
		y += u.y;
		z += u.z;
		return this;
	}
	
	public final Vector3D subtract(Vector3D u) {
		x -= u.x;
		y -= u.y;
		z -= u.x;
		return this;
	}
	
	public final Vector3D multiply(float s) {
		x *= s;
		y *= s;
		z *= s;
		return this;
	}
	
	public final Vector3D divide(float s) {
		x /= s;
		y /= s;
		z /= s;
		return this;
	}

	public final Vector3D conjugate() {
		return new Vector3D(-x, -y, -z);
	}
	
	/** Return polar orientation (in degrees from 0 - 360) from one Vector3D to another. */
	public final double orientTo(Vector3D to) {
		return Utility.orientTo(x, y, to.x, to.y);
	}

	public static final Vector3D add(Vector3D u, Vector3D v) {
		return new Vector3D(u.x + v.x, u.y + v.y, u.z + v.z);
	}
	
	public static final Vector3D subtract(Vector3D u, Vector3D v) {
		return new Vector3D(u.x - v.x, u.y - v.y, u.z - v.z);
	}
	
	/** ^ */
	public static final Vector3D crossProduct(Vector3D u, Vector3D v) {
		return new Vector3D(u.y * v.z - u.z * v.y, -u.x * v.z + u.z * v.x, u.x * v.y - u.y * v.x);
	}
	
	/** * */
	public static final float dotProduct(Vector3D u, Vector3D v) {
		return (u.x * v.x + u.y * v.y + u.z * v.z);
	}
	
	public static final Vector3D multiply(float s, Vector3D u) {
		return new Vector3D(u.x * s, u.y * s, u.z * s);
	}
	
	public static final Vector3D multiply(Vector3D u, float s) {
		return new Vector3D(u.x * s, u.y * s, u.z * s);
	}
	
	public static final Vector3D divide(Vector3D u, float s) {
		return new Vector3D(u.x / s, u.y / s, u.z / s);
	}
	
	public static final float tripleScalarProduct(Vector3D u, Vector3D v, Vector3D w) {
		return (u.x * (v.y * w.z - v.z * w.y)) +
		       (u.y * (-v.x * w.z + v.z * w.x)) +
		       (u.z * (v.x * w.y - v.y * w.x)); 
	}

	public static final Vector3D vRotate2D(float angleInDegrees, Vector3D u) {
		double x, y;
		double degrees = - Utility.degreesToRadians(angleInDegrees);
		x = u.x * Math.cos(degrees) + u.y * Math.sin(degrees);
		y = -u.x * Math.sin(degrees) + u.y * Math.cos(degrees);
		return new Vector3D((float)x, (float)y, 0);
	}
	
	/** Return the distance between two Vector3Ds */
	public static final float getDistance(Vector3D p1, Vector3D p2) {
		return Vector3D.subtract(p1, p2).getMagnitude();
	}

	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}
	
}
