package uk.ac.derby.Tanq.Brains;

import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.Core.Tanq;

/** Opponent provides information about an opponent without allowing the programmer
 * to cheat by having direct access to a Tanq.  In a production game, it would be perfectly
 * acceptable to allow the AIs to cheat, but given the competitive setting of this game, 
 * it is obviously preferable to prevent it.
 */
public class Opponent {

	private Tanq tanq;
	
	public Opponent(Tanq t) {
		tanq = t;
	}
	
	public String getName() {
		return tanq.getSpriteName();
	}
	
	public Vector3D getLocation() {
		// Copy of Vector3D ensures user can't move the opponent.
		return new Vector3D(tanq.getLocation());
	}
	
	/** Obtain pure velocity. */
	public float getVelocity() {
		return tanq.getBody().getVelocity();
	}
	
	/** Obtain orientation in degrees. */
	public float getOrientationInDegrees() {
		return tanq.getBody().getOrientationInDegrees();
	}
	
	/** Obtain velocity/direction vector. */
	public Vector3D getVelocityVector() {
		// Copy of Vector3D ensures user can't change speed/direction of opponent.
		return new Vector3D(tanq.getBody().getVelocityVector());
	}
	
	public int getScore() {
		return tanq.getScore();
	}
	
	public int getInjuries() {
		return tanq.getInjuries();
	}

	public int getCollisions() {
		return tanq.getCollisions();
	}
	
	public int getBumps() {
		return tanq.getBumps();
	}
	
	public int getGunCurrentTemp() {
		return tanq.getGun().getCurrentTemp();
	}
	
	public String toString() {
		return getName();
	}
	
}
