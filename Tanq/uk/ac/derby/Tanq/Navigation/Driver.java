package uk.ac.derby.Tanq.Navigation;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.*;
import java.awt.event.*;

import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.Brains.*;

/* A Driver will drive a specified Brain given a path of Vector3D points.  These may be specified in real time.
 * 
 * Braking is used to smooth out the movement, and crudely takes into account the cornering angle.  Shallow
 * turns do not require us to slow down as much.  This sometimes results in "hunting" for the destination,
 * so further refinement is obviously needed.
 */
public class Driver implements BrainListener {

	private final static float cornerTolerance = 10.0f;		// close enough...
	private final static float brakingRange = 6.0f;			// meaningless unit relative to velocity
	private final static float creepingVelocity = 0.05f;	// veeeeery slow

	private Brain brain;
	private boolean driving = false;
	
	private Queue<Vector3D> path = new LinkedBlockingQueue<Vector3D>();		// Queue containing the path waypoints
	
	private float cruisingVelocity = 6.0f;						// maximum speed
	private float corneringVelocity = creepingVelocity;			// speed with which we take sharp corners

	private float brakingDistance = cruisingVelocity * brakingRange;	// braking distance
		
	private Vector3D previousDestination = null;
	private Vector3D nextDestination = null;
	private Vector3D currentDestination = null;
	
	private boolean corneringAdjustmentEnabled = true;
	
	/** Create a Driver for a given Brain. */
	public Driver(Brain aBrain) {
		brain = aBrain;
		brain.addListener(this);
	}
	
	/** Get the Brain that this Driver is controlling. */
	public final Brain getBrain() {
		return brain;
	}
	
	/** True if cornering speed adjustment is enabled. */
	public void setCorneringAdjustmentEnabled(boolean flag) {
		corneringAdjustmentEnabled = flag;
	}
	
	/** True if cornering speed adjustment is enabled. */
	public boolean isCorneringAdjustmentEnabled() {
		return corneringAdjustmentEnabled;
	}
	
	/** Start driving. */
	public void start() {
		if (driving)
			return;
		(new Thread() {
			public void run() {
				driving = true;
				while (driving) {
					move();
					yield();	// prevent consuming all CPU resources
				}
			}
		}).start();
	}
	
	/** Stop driving. */
	public void stop() {
		driving = false;
	}
	
	/** Add the given point to the current path. */
	public void addToPath(Vector3D point) {
		path.offer(point);
	}
	
	/** Flush the current path and start over. */
	public void clearPath() {
		path.clear();
		nextDestination = null;
		currentDestination = null;
	}
	
	/** Set the speed limit. */
	public void setSpeedLimit(float aVelocity) {
		cruisingVelocity = aVelocity;
	}
	
	/** Get the current speed limit. */
	public float getSpeedLimit() {
		return cruisingVelocity;
	}
	
	/** This method is invoked when the Tanq reaches the end of the path, or before it has
	 * any path at all. */
	public void notifyReachedDestination() {}
	
	/** This method is invoked when the Tanq is within two waypoints of the end of the path.  This
	 * can be used to add a waypoint to the path, and will keep a sufficient number of points in the
	 * path to compute cornering angles. */
	public void notifyNearingDestination() {}
	
	/** Invoked when hit by a bullet. */
	public void notifyInjury(Opponent shooter) {}
	
	/** Invoked when an opponent is hit by one of our bullets. */
	public void notifyScored(Opponent victim) {}

	/** Invoked when we bump into an opponent, or an opponent bumps into us. */
	public void notifyBumped(Opponent opponent) {
		boing();
	}
	
	/** Invoked when we bump into terrain or arena edges. */
	public void notifyBlockedByTerrain() {
		boing();
	}
	
	private boolean boinging = false;

	// Boing timer is used to control how long we "boing" (see below) after a collision.
	private Timer boingTimer = new Timer(250, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			boinging = false;
		}
	});
	
	/** We've either hit, or been hit by an opponent or terrain.  Fly off in a random direction 
	 * for a little while.  We call that a "boing." */
	protected void boing() {		
		// Fly off in a random direction
		brain.setDirection(Math.random() * 360, cruisingVelocity);
		if (boinging)
			return;
		// Start the boing timer
		boinging = true;
		boingTimer.setRepeats(false);
		boingTimer.start();
	}
	
	private boolean notifiedReachedDestination = false;
	
	/** Handle movement. */
	protected void move() {
		if (boinging) {		// while boinging, don't do anything but boing.
			return;
		}
		// Acquire destination, if we don't have one already
		if (currentDestination == null) {
			if (path.isEmpty()) {
				if (brain.getVelocity() > Utility.nearZero) {
					brain.setAppliedForce(brain.getVelocityVector().conjugate());
					if (previousDestination != null)
						brain.setLocation(previousDestination);
				}
				if (!notifiedReachedDestination) {
					notifiedReachedDestination = true;
					notifyReachedDestination();					
				}
				return;
			}
			currentDestination = (Vector3D)path.remove();
		}
		notifiedReachedDestination = false;
		nextDestination = (Vector3D)path.peek();
		// Get distance to destination
		float destinationDistance = Vector3D.subtract(currentDestination, brain.getLocation()).getMagnitude(); 
		// Are we close enough to the destination to move to next destination?
		// The faster we go, the less we care about accuracy.
		if (brain.getVelocity() < corneringVelocity && destinationDistance < ((path.isEmpty()) ? cornerTolerance : cornerTolerance * brain.getVelocity())) {
			// get next destination
			previousDestination = currentDestination;
			if (path.isEmpty()) {
				currentDestination = null;
				nextDestination = null;
			} else {
				currentDestination = (Vector3D)path.remove();
				nextDestination = (Vector3D)path.peek();
				if (path.size() < 3)
					notifyNearingDestination();
			}
			// Get cornering angle around current destination
			if (currentDestination != null && nextDestination != null && previousDestination != null) {
				double cornerAngle = Utility.getRelativeAngle(nextDestination, previousDestination, currentDestination);
				// The closer we are to 180 degrees (a straight line), the faster we can go around the corner
				// adjustment = 1 means corner has no turn at all.  0 means a 180 degree reversal of direction.
				double adjustment = 1.0 - Math.abs(180.0 - cornerAngle) / 180.0;
				// Relationship isn't linear, so only adjust if we can go pretty fast
				if (adjustment > 0.5 && corneringAdjustmentEnabled)
					corneringVelocity = ((float)adjustment * (cruisingVelocity - creepingVelocity)) + creepingVelocity;
				else
					corneringVelocity = creepingVelocity;
			} else
				corneringVelocity = creepingVelocity;
			if (brain.isDebugging())
				brain.println("Cornering speed: " + corneringVelocity);
			brakingDistance = cruisingVelocity * brakingRange;
		}
		// Are we close enough to the destination to apply brakes?
		if (destinationDistance < brakingDistance && brain.getVelocity() > corneringVelocity * destinationDistance) 
			brain.setAppliedForce(brain.getVelocityVector().conjugate().multiply(2.0f));
		else if (currentDestination != null) {
			// Steer toward destination
			// Obtain angle between us and the destination
			double angleToDestination = brain.getLocation().orientTo(currentDestination);
			brain.setAppliedForce(Vector3D.vRotate2D((float)angleToDestination, new Vector3D(cruisingVelocity, 0.0f, 0.0f)));
			brakingDistance = brain.getVelocity() * brakingRange;
		} else
			brain.setAppliedForce(new Vector3D(0f, 0f, 0f));
	}

}
