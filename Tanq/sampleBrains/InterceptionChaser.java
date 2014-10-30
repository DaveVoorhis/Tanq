package sampleBrains;

import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.Brains.Brain;

/* 
 * This defines a Brain that chases using interception.  I.e., it aims itself
 * where it thinks the opponent is going to be.
 */
public class InterceptionChaser extends Brain {
	
	private Brain opponent = null;
	
	/** Invoke this to set an opponent. */
	public void setOpponent(Brain enemy) {
		opponent = enemy;
	}
	
	/** Invoked continuously.  Conscious behaviour goes here. */
	public void compute() {
		if (opponent == null)
			return;
		
		// Calculate the relative velocity between ourselves and the opponent
		// This is called the closing velocity.
		Vector3D velocityClosing = Vector3D.subtract(opponent.getVelocityVector(), getVelocityVector());

		System.out.println("velocityClosing = " + velocityClosing);		
		
		if (velocityClosing.getMagnitude() < Utility.nearZero)
			return;
		
		// Now calculate the range to close, which is the relative distance between
		// us and the opponent.
		Vector3D rangeToClose = Vector3D.subtract(opponent.getLocation(), getLocation());
		
		System.out.println("rangeToClose = " + rangeToClose);		
		
		// This is the time to close, which is the average time it will take to
		// travel a distance equal to rangeToClose while travelling at a
		// velocityClosing speed.
		float timeToClose = rangeToClose.getMagnitude() / velocityClosing.getMagnitude();

		System.out.println("timeToClose = " + timeToClose);		
		
		// Now that we know the timeToClose, we can predict where the opponent will
		// be at timeToClose in the future.  The current position of the opponent
		// is opponent.getLocation(), and it is travelling at opponent.getVelocityVector()
		// speed.  Because speed times time gives an average distance, we can calculate
		// how far the opponent will travel over timeToClose, travelling at
		// opponent.getVelocityVector() speed, and add it to its current position to
		// get its predicted position.
		Vector3D predictedPosition = Vector3D.add(opponent.getLocation(), Vector3D.multiply(opponent.getVelocityVector(), timeToClose));

		System.out.println("InterceptionChaser: current posn=" + opponent.getLocation() + " predicted posn=" + predictedPosition);
		
		// Obtain angle between us and the prey's predicted position
		double angleToPrey = getLocation().orientTo(predictedPosition);

		// Fire as continuously as gun heating will allow.
		if (getGunCurrentTemp() < getGunUnjamTemp())
			fire((float)angleToPrey);

		// Steer toward prey's predicted position.
		setDirection(angleToPrey, 1.0f);
	}
	
}
