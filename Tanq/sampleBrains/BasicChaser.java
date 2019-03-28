package sampleBrains;

import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.Brains.Brain;

/* 
 * This defines a very crude Brain that chases using the simplest possible mechanism.
 */
public class BasicChaser extends Brain {

	private Brain opponent = null;
	
	/** Invoke this to set an opponent. */
	public void setOpponent(Brain enemy) {
		opponent = enemy;
	}
	
	/** Invoked continuously.  Conscious behaviour goes here. */
	public void compute() {
		if (opponent == null) {
			return;
		}
		Vector3D myLocation = getLocation();
		Vector3D opponentLocation = opponent.getLocation();
		float myX = myLocation.getX(), myY = myLocation.getY();
		if (myX > opponentLocation.getX())
			myX -= 1;
		else if (myX < opponentLocation.getX())
			myX += 1;
		if (myY > opponentLocation.getY())
			myY -= 1;
		else if (myY < opponentLocation.getY())
			myY += 1;
		setLocation(new Vector3D(myX, myY, 0));
		Utility.delay(75);
	}
	
}
