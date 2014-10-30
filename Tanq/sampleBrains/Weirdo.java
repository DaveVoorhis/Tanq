package sampleBrains;

import uk.ac.derby.GameEngine2D.Utility;
import uk.ac.derby.Tanq.Brains.Brain;

/*
 * A 'Weirdo' is a simple Tanq brain that randomly moves using steering
 * trust instead of applied force for movement.
 * 
 */
public class Weirdo extends Brain {
	
	boolean fires;
	
	public Weirdo(boolean firesGun) {
		fires = firesGun;
	}
	
	/** Invoked once */
	public void initialise() {
		(new Thread() {
			public void run() {
				while (true) {
					setThrottle((float)(Math.random() * 10.0));
					setSteering(Math.random() > 0.5, Math.random() > 0.5);
            		Utility.delay((int)(2000 * Math.random()));
				}
			}
		}).start();
	}

	/** Invoked continuously.  Conscious behaviour goes here. */
	public void compute() {
		// Fire as continuously as gun heating will allow.
		if (getGunCurrentTemp() < getGunUnjamTemp() && fires)
			fire((float)(Math.random() * 360.0));
		// Might as well wait the gun can be reloaded
		Utility.delay(getGunReloadDurationMillis());
	}
	
}
