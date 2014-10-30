package sampleBrains;

import uk.ac.derby.Tanq.Brains.Brain;

/* 
 * This defines a Brain that chases using line-of-sight.  I.e., it aims itself
 * at the opponent and it shoots at the opponent.
 */
public class LineOfSightChaser extends Brain {
	
	private Brain opponent = null;
	
	/** Invoke this to set an opponent. */
	public void setOpponent(Brain enemy) {
		opponent = enemy;
	}
	
	/** Invoked continuously.  Conscious behaviour goes here. */
	public void compute() {
		if (opponent == null)
			return;

		// Obtain angle between us and the prey
		double angleToPrey = getLocation().orientTo(opponent.getLocation());

		// Fire as continuously as gun heating will allow.
		if (getGunCurrentTemp() < getGunUnjamTemp())
			fire((float)angleToPrey);

		// Steer toward prey
		setDirection(angleToPrey, 1.0f);
	}
	
}
