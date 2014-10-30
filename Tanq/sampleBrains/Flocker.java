package sampleBrains;

import uk.ac.derby.GameEngine2D.Vector3D;
import uk.ac.derby.Tanq.Brains.Brain;
import uk.ac.derby.Tanq.Brains.Opponent;

/** A Brain that Flocks */
public class Flocker extends Brain {

	private final static int recalcInterval = 10;
	private final static int velocity = 5;
	private final static int avoidBumpDistance = 20;
	
	private int recalcCount = 0;
	
	private final static int LONGDISTANCE = 99999;
	
	private Brain leader;
	
	public Flocker(Brain leader) {
		this.leader = leader;
	}
	
	/** Get average location of opponents.  Return null if there aren't any. */
	public Vector3D getAverageLocationOfOpponents(Opponent[] opponents) {
		float totalX = 0;
		float totalY = 0;
		if (opponents.length == 0)
			return null;
		for (Opponent opponent: opponents) {
			Vector3D opponentLocation = opponent.getLocation();
			totalX += opponentLocation.getX();
			totalY += opponentLocation.getY();
		}
		return new Vector3D(totalX / opponents.length, totalY / opponents.length, 0);
	}

	/** Get average direction of opponents.  Return -1 if there aren't any. */
	public float getAverageDirectionOfOpponents(Opponent[] opponents) {
		if (opponents.length == 0)
			return -1;
		float totalDirection = 0;
		for (Opponent opponent: opponents) {
			totalDirection += opponent.getOrientationInDegrees();
		}
		return totalDirection / opponents.length;
	}

	private Opponent nearestOpponent;
	
	// Get distance to nearest opponent
	private int getDistanceToNearestOpponent() {
		nearestOpponent = null;
		int distance = LONGDISTANCE;
		Opponent[] opponents = getOpponents();
		for (int i=0; i<opponents.length; i++) {
			int d = (int)Vector3D.getDistance(getLocation(), opponents[i].getLocation());
			if (d < distance) {
				distance = d;
				nearestOpponent = opponents[i];
			}
		}
		return distance;
	}
	
	// Get nearest opponent
	private Opponent getNearestOpponent() {
		return nearestOpponent;
	}
	
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
	
	// Get direction based on cohesion
	private double getCohesionDirection(Opponent[] opponents) {
		Vector3D averageLocation = getAverageLocationOfOpponents(opponents); 
		if (averageLocation == null)
			return 0;
		return getLocation().orientTo(averageLocation);
	}

	// Get direction based on alignment
	private double getAlignmentDirection(Opponent[] opponents) {
		double averageDirection = getAverageDirectionOfOpponents(opponents);		
		if (averageDirection == -1)
			return 0;
		return averageDirection;
	}
	
	// Get direction to leader
	private double getDirectionToLeader() {
		return getLocation().orientTo(leader.getLocation());
	}
	
	/** Invoked continuously.  Conscious behaviour goes here. */
	public void compute() {
		recalcCount++;
		if (recalcCount < recalcInterval)
			return;
		int distanceToNearestOpponent = getDistanceToNearestOpponent();
		if (distanceToNearestOpponent < avoidBumpDistance) {
			setDirection(getLocation().orientTo(getNearestOpponent().getLocation()) + 180, velocity);
		} else {
			Opponent[] opponents = getOpponents();
			setDirection(
				(
					getCohesionDirection(opponents) * 1.0 + 
					getAlignmentDirection(opponents) * 1.0 +
					getDirectionToLeader() * 2.0
				) / 4.0, velocity);
		}
		recalcCount = 0;
	}
	
}
