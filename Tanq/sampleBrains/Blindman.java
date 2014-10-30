package sampleBrains;

import uk.ac.derby.Tanq.Brains.Brain;
import uk.ac.derby.Tanq.Brains.Opponent;

/** A trivial Brain that makes a Tanq head in a random direction, at a random velocity,
 * until it hits something.
 */
public class Blindman extends Brain {
	
	/** Invoked once */
	public void initialise() {
		// Head in a random direction, with a random
		// throttle setting
		setDirection(Math.random() * 360, Math.random() * 5.0);
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
	}
	
}
