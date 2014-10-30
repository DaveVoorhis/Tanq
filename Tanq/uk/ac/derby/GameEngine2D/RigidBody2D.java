package uk.ac.derby.GameEngine2D;

/** A class defining simple rigid (i.e., non-elastic) 2D physics. */
public class RigidBody2D {

	public static final float _MAXTHRUST = 10.0f;
	public static final float _MINTHRUST = 0.0f;
	public static final float _DTHRUST = 0.01f;
	public static final float _STEERINGFORCE = 3.0f;
	public static final float _LINEARDRAGCOEFFICIENT = 0.3f;
	public static final float _ANGULARDRAGCOEFFICIENT = 5000.0f;

	private float fMass = 10;			// total mass (constant)
	private float fInertia = 10;		// mass moment of inertia in body coordinates (constant)
	
	private Vector3D vPosition = new Vector3D();	// position in world coordinates
	private Vector3D vVelocity = new Vector3D();	// velocity in world coordinates
	private Vector3D vVelocityBody = new Vector3D();		// velocity in body coordinates
	private Vector3D vAngularVelocity = new Vector3D();	// angular velocity in body coordinates
		
	private float fOrientation;			// orientation 	

	private Vector3D vForces = new Vector3D();			// total force on body
	private Vector3D vMoment = new Vector3D();			// total moment (torque) on body (2D: about z-axis only)

	private float driveThrust;			// Magnitude of the thrust force
	private Vector3D portThrust = new Vector3D();	// steering thruster forces
	private Vector3D starboardThrust = new Vector3D();
	
	private Vector3D centerOfDrag = new Vector3D();		// in body coordinates
	private Vector3D centerPortSteeringThruster = new Vector3D(-1, 1, 0);
	private Vector3D centerStarboardSteeringThruster = new Vector3D(1, 1, 0);

	private float fProjectedArea = 250;	// Projected area causes wind drag

	private Vector3D Fa = new Vector3D();					// other applied force
	private Vector3D Pa = new Vector3D();					// location of other applied force

	/** Create a rigid body. */
	public RigidBody2D() {
	}

	/** Reset all movement and force vectors. */
	public void reset() {
		vVelocity.reset();
		vVelocityBody.reset();
		vAngularVelocity.reset();
		fOrientation = 0;
		vForces.reset();
		vMoment.reset();
		driveThrust = 0;
		portThrust.reset();
		starboardThrust.reset();
		Fa.reset();
		Pa.reset();
	}
	
	/** Create a rigid body with an initial position. */
	public RigidBody2D(float x, float y) {
		setPosition(new Vector3D(x, y, 0));
	}
	
	/** Set body mass.  Default = 10 */
	public void setMass(float f) {
		fMass = f;
	}
	
	/** Get body mass. */
	public float getMass() {
		return fMass;
	}
	
	/** Set inertia.  Default = 10 */
	public void setInertia(float f) {
		fInertia = f;	// eg 10
	}
	
	/** Get inertia. */
	public float getInertia() {
		return fInertia;
	}

	/** Set projected (surface) area */
	public void setProjectedArea(float f) {
		fProjectedArea = f;
	}
	
	/** Get projected area */
	public float getProjectedArea() {
		return fProjectedArea;
	}
	
	/** Set position in world. */
	public void setPosition(Vector3D v) {
		vPosition = v;
	}
	
	/** Get position in world. */
	public Vector3D getPosition() {
		return vPosition;
	}
	
	/** Calculate loads, forces, all that physics stuff. */
	private void calcLoads() {
		Vector3D Fb = new Vector3D();			// stores the sum of forces
		Vector3D Mb = new Vector3D();			// stores the sum of moments
		Vector3D Thrust = new Vector3D();		// thrust vector
		
		// reset forces and moments:
		vForces.reset();
		vMoment.reset();
		
		// Define the thrust vector, which acts through the craft's CG
		Thrust = new Vector3D(0.0f, 1.0f, 0.0f);
		Thrust.multiply(driveThrust);
		
		// Calculate forces and moments in body space:
		// Calculate the aerodynamic drag force:
		// Calculate local velocity:
		// The local velocity includes the velocity due to linear motion of the craft, 
		// plus the velocity at each element due to the rotation of the craft.
		Vector3D vLocalVelocity = Vector3D.add(vVelocityBody, Vector3D.crossProduct(vAngularVelocity, centerOfDrag));

		// Calculate local air speed
		float fLocalSpeed = vLocalVelocity.getMagnitude();

		// Find the direction in which drag will act.
		// Drag always acts in line with the relative velocity but in the opposing direction
		if (fLocalSpeed > Utility.nearZero)  {
			vLocalVelocity.normalise();
			Vector3D vDragVector = vLocalVelocity.conjugate();		

			// Determine the resultant force on the element.
			double f;
			if (Vector3D.dotProduct(Thrust, vLocalVelocity) / (Thrust.getMagnitude() * vLocalVelocity.getMagnitude()) > 0)
				f = 2;	
			else
				f = 1;

			float tmp = 0.5f * Utility.rho * fLocalSpeed * fLocalSpeed * fProjectedArea * (float)f;
			Vector3D vResultant = Vector3D.multiply(Vector3D.multiply(vDragVector, _LINEARDRAGCOEFFICIENT), tmp); // simulate fuselage drag

			// Keep a running total of these resultant forces (total force)
			Fb.add(vResultant);
			
			// Calculate the moment about the CG of this element's force
			// and keep a running total of these moments (total moment)
			Mb.add(Vector3D.crossProduct(centerOfDrag, vResultant));
		}

		// Calculate the Port & Starboard bow thruster forces:
		// Keep a running total of these resultant forces (total force)
		Fb.add(Vector3D.multiply(3, portThrust));

		// Calculate the moment about the CG of this element's force
		// and keep a running total of these moments (total moment)
		Mb.add(Vector3D.crossProduct(centerPortSteeringThruster, portThrust));

		// Keep a running total of these resultant forces (total force)
		Fb.add(Vector3D.multiply(3, starboardThrust));

		// Calculate the moment about the CG of this element's force
		// and keep a running total of these moments (total moment)
		Mb.add(Vector3D.crossProduct(centerStarboardSteeringThruster, starboardThrust));

		// do other applied forces here
		Fb.add(Fa);
		Mb.add(Vector3D.crossProduct(Pa, Fa));

		// Calculate rotational drag
		if (vAngularVelocity.getMagnitude() > Utility.nearZero) {
			Vector3D vtmp = new Vector3D();	
			vtmp.setX(0);
			vtmp.setY(0);
			float tmp = 0.5f * Utility.rho * vAngularVelocity.getZ() * vAngularVelocity.getZ() * fProjectedArea;
			if (vAngularVelocity.getZ() > 0.0)
				vtmp.setZ(-_ANGULARDRAGCOEFFICIENT * tmp);		
			else
				vtmp.setZ(_ANGULARDRAGCOEFFICIENT * tmp);		
			Mb.add(vtmp);
		}

		// Now add the propulsion thrust
		Fb.add(Thrust); 	// no moment since line of action is through CG

		// Convert forces from model space to world space
		vForces = Vector3D.vRotate2D(fOrientation, Fb);
		vMoment.add(Mb);			
	}
	
	/** Update body position given a relative change in time. */
	public void updateBodyEuler(double dt) {
		// Calculate forces and moments:
		calcLoads();
		
		// Integrate linear equation of motion:
		vVelocity.add(Vector3D.multiply(Vector3D.divide(vForces, fMass), (float)dt));
		vPosition.add(Vector3D.multiply(vVelocity, (float)dt));		
		vAngularVelocity.setZ(vAngularVelocity.getZ() + vMoment.getZ() / fInertia * (float)dt);		
		fOrientation += Utility.radiansToDegrees(vAngularVelocity.getZ() * (float)dt);
		// Misc. calculations:
		vVelocityBody = Vector3D.vRotate2D(-fOrientation, vVelocity);			
	}
	
	/** Obtain velocity in world space. */
	public float getVelocity() {
		return vVelocity.getMagnitude();
	}

	/** Obtain velocity in world space as a vector. */
	public Vector3D getVelocityVector() {
		return vVelocity;
	}
	
	/** Obtain body orientation in degrees. */
	public float getOrientationInDegrees() {
		Vector3D v = new Vector3D(vVelocity);
		Vector3D v2 = Vector3D.multiply(v, 2);
		return (float)v.orientTo(v2);
	}
	
	/** Obtain position in world. */
	public float getX() {
		return vPosition.getX();
	}
	
	/** Obtain position in world. */
	public float getY() {
		return vPosition.getY();
	}
	
	public void setSteeringThrusters(boolean port, boolean starboard) {
		portThrust.setX(0);
		portThrust.setY(0);
		starboardThrust.setX(0);
		starboardThrust.setY(0);		
		if (port)
			portThrust.setX(-_STEERINGFORCE);
		if (starboard)
			starboardThrust.setX(_STEERINGFORCE);	
	}
	
	public void setThrust(boolean up) {
		driveThrust += up ? _DTHRUST : -_DTHRUST;
		if (driveThrust > _MAXTHRUST) driveThrust = _MAXTHRUST;
		if (driveThrust < _MINTHRUST) driveThrust = _MINTHRUST;		
	}

	public void setThrust(float thrust) {
		driveThrust = thrust;
		if (driveThrust > _MAXTHRUST) driveThrust = _MAXTHRUST;
		if (driveThrust < _MINTHRUST) driveThrust = _MINTHRUST;		
	}

	/** Set additional applied force. */
	public void setAppliedForce(Vector3D fA) {
		Fa = fA;
	}

	/** Get additional applied force. */
	public Vector3D getAppliedForce() {
		return Fa;
	}
	
	/** Set body position of additional applied force. */
	public void setAppliedForcePosition(Vector3D pA) {
		Pa = pA;
	}
	
	/** Get body position of additional applied force. */
	public Vector3D getAppliedForcePosition() {
		return Pa;
	}
}
