package sampleBrains;

import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.Brains.*;

/*
 * A 'Madman' is a simple Tanq brain that demonstrates minimal intelligence.
 * 
 * <p>It moves randomly and fires at the nearest enemy.
 * 
 * <p>See uk.ac.derby.Tanq.Brain for information on how to control a Tanq.
 * 
 * <p>See uk.ac.derby.GameEngine2D.Vector3D for vector manipulation.
 * 
 */
public class Madman extends Brain {
	
	/** Invoked once */
	public void initialise() {
		// An example of launching a background thread to 
		// perform Brain functions.  (The subconscious?)
		(new Thread() {
			public void run() {
				while (true) {
					// Head in a new random direction, with a random
					// throttle setting
					setDirection(Math.random() * 360, Math.random() * 5.0);
					// Wait between 0 and 2 seconds before changing direction
            		Utility.delay((int)(2000 * Math.random()));
				}
			}
		}).start();
	}
	
	/** Invoked when we bump into terrain or arena edges. */
	public void notifyBlockedByTerrain() {
		// Zing off in new direction
		setDirection(Math.random() * 360, Math.random() * 5.0);
	}

	/** Invoked when we bump into another Tanq. */
	public void notifyBumped(Opponent who) {
		// Zing off in new direction
		setDirection(Math.random() * 360, Math.random() * 5.0);
	}

	/** Invoked continuously.  Conscious behaviour goes here. */
	public void compute() {
		// Find nearest enemy
		Opponent nearest = null;
		float nearestDistance = 9999999f;
		Opponent[] opponents = getOpponents();
		for (int i = 0; i<opponents.length; i++) {
			float distance = Vector3D.subtract(opponents[i].getLocation(), getLocation()).getMagnitude();
			if (distance < nearestDistance) {
				nearest = opponents[i];
				nearestDistance = distance;
			}
		}
		// Fire at nearest enemy as continuously as gun heating will allow.
		if (getGunCurrentTemp() < getGunUnjamTemp() && nearest != null)
			fire((float)getLocation().orientTo(nearest.getLocation()));
		// Might as well wait until the gun can be reloaded
		Utility.delay(getGunReloadDurationMillis());
	}
	
}
