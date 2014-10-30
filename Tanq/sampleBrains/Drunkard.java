package sampleBrains;

import uk.ac.derby.GameEngine2D.Utility;
import uk.ac.derby.Tanq.Brains.Brain;
import uk.ac.derby.Tanq.Brains.Opponent;

/** A trivial Brain that makes a Tanq wander randomly.
 */
public class Drunkard extends Brain {
	
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
		// Head in a new random direction, with a random
		// throttle setting
		setDirection(Math.random() * 360, Math.random() * 5.0);
		// Wait between 0 and 2 seconds before changing direction
		Utility.delay((int)(2000 * Math.random()));
	}
	
}
