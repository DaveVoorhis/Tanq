package sampleBrains;

import uk.ac.derby.Tanq.Brains.Brain;
import uk.ac.derby.Tanq.Brains.Opponent;
import uk.ac.derby.Tanq.Navigation.Navigator;
import uk.ac.derby.GameEngine2D.Vector3D;

/**
 * A Finite State Machine based Brain.
 */

public class FiniteStateMachine extends Brain {
	
	public final int IDLING_TO_SEEKING_DISTANCE = 400;
	public final int SEEKING_TO_ATTACKING_DISTANCE = 150;
	
	public final int STATE_IDLING = 0;
	public final int STATE_SEEKING = 1;
	public final int STATE_ATTACKING = 2;
	public final int STATE_RETREATING = 3;
	public final int STATE_DEFENDING = 4;
	
	private final int LONGDISTANCE = 99999;
	
	// Current state
	private int state = -1;
	
	// Additional state to indicate acquired target
	private Opponent target = null;

	// Navigator used to drive the Tanq
	private Navigator navigator = null;

	public String toString() {
		switch (state) {
			case STATE_IDLING: return "idling";
			case STATE_SEEKING: return "seeking";
			case STATE_ATTACKING: return "attacking";
			case STATE_RETREATING: return "retreating";
			case STATE_DEFENDING: return "defending";			
		}
		return "???";
	}

	private boolean destinationReached = true;
	
	public void initialise() {
		navigator = new Navigator(this, 0.1f, true, 2) {
			public void notifyReachedDestination() {
				println("Destination reached.");
				destinationReached = true;
			}
		};
		navigator.setSpeedLimit(3.0f);
		setDebugging(false);
		setState(STATE_IDLING, "this is the initial state.");
	}
	
	public void compute() {
		switch (state) {
			case STATE_IDLING: idling(); break;
			case STATE_SEEKING: seeking(); break;
			case STATE_ATTACKING: attacking(); break;
			case STATE_RETREATING: retreating(); break;
			case STATE_DEFENDING: defending(); break;
		}
	}
	
	private void setState(int newState, String why) {
		if (newState == state)
			return;
		state = newState;
		println("Set state to " + toString() + " because " + why + ".");
	}
	
	private boolean hitByOpponent = false;
	
	/** Invoked when struck by enemy bullet. */
	public void notifyInjury(Opponent shooter) {
		hitByOpponent = true;
	}
	
	/** Invoked when bumped. */
	public void notifyBump(Opponent bumper) {
		hitByOpponent = true;
	}

	/** Clear 'hitByOpponent' */
	public void clearHitByOpponent() {
		hitByOpponent = false;
	}
	
	public void setDestination(Vector3D where) {
		println("Set new destination: " + where);
		navigator.stop();
		destinationReached = false;
		navigator.setDestination(where);
	}
	
	// True if the gun is ready to fire
	private boolean isReadyToFire() {
		return !isGunJammed() && getGunBulletsAvailable() > 0;
	}
	
	/*   Idling - pick an opponent and chase him. */
	public void idling() {
		if (getDistanceToNearestOpponent() < IDLING_TO_SEEKING_DISTANCE) {
			setTarget(getNearestOpponent());
			setState(STATE_SEEKING, target + " is within seeking distance while idling");
			return;
		}
		moveRandomly();
	}
	
	/*   Seeking - if an enemy Tanq comes within a given distance, 
                   use pathfinding to chase it; */
	public void seeking() {
		if (getDistanceToTarget() < SEEKING_TO_ATTACKING_DISTANCE) {
			setState(STATE_ATTACKING, "distance to " + target + " is within attacking distance while seeking");
			return;
		} else if (getDistanceToTarget() > IDLING_TO_SEEKING_DISTANCE) {
			setState(STATE_IDLING, "distance to target is outside seeking distance while seeking");
			return;
		}
		moveTowardTarget();
	}

	/*   Attacking - if an enemy Tanq is close, engage it in battle, 
                     using gun to attack it; */
	public void attacking() {
		if (getDistanceToTarget() > SEEKING_TO_ATTACKING_DISTANCE) {
			setState(STATE_SEEKING, "distance to " + target + " is outside attacking distance while attacking");
			return;
		}
		if (isGunLoaded())
			attackTarget();
		if (!isReadyToFire()) {
			setState(STATE_RETREATING, "gun is jammed or empty while attacking");
			return;
		}
		if (isHitByOpponent()) {
			setState(STATE_DEFENDING, "I've been shot by an opponent while attacking");
			return;
		}
	}
	
	/* Retreat - if gun has overheated or run empty, temporarily retreat from enemy; */
	public void retreating() {
		if (isHitByOpponent()) {
			setState(STATE_DEFENDING, "I've been shot by an opponent while retreating");
			return;
		}
		if (getDistanceToTarget() > SEEKING_TO_ATTACKING_DISTANCE) {
			setState(STATE_SEEKING, "distance to " + target + " is outside attacking distance while retreating");
			return;
		} else if (isReadyToFire()) {
			setState(STATE_ATTACKING, "gun has un-jammed or obtained bullets while retreating");
			return;
		}
		evadeTarget();
	}
	
	public void defending() {
		if (getDistanceToTarget() < IDLING_TO_SEEKING_DISTANCE) {
			evadeTarget();
			return;
		}
		setState(STATE_IDLING, "distance to " + target + " is outside seeking distance while defending");
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
	
	// Set target
	private void setTarget(Opponent t) {
		println("My target is " + t);
		target = t;
	}
	
	// Move randomly
	private void moveRandomly() {
		if (destinationReached)
			setDestination(getRandomLocation());
	}
	
	// Move toward target
	private void moveTowardTarget() {
		if (destinationReached && target != null)
			setDestination(target.getLocation());
	}
	
	// Attack target
	private void attackTarget() {
		if (target == null)
			return;
		fire((float)getLocation().orientTo(target.getLocation()));
	}
	
	// Return true if hit by opponent recently.
	// NOTE:  This is currently a no-op.
	private boolean isHitByOpponent() {
		if (hitByOpponent) {
			clearHitByOpponent();
			return true;
		} else
			return false;
	}
	
	// Get distance to target 
	private int getDistanceToTarget() {
		if (target==null)
			return LONGDISTANCE;
		return (int)Vector3D.getDistance(getLocation(), target.getLocation());
	}
	
	// Evade target
	// NOTE:  Currently, this merely moves randomly, but it should
	//        move away from the target.
	private void evadeTarget() {
		moveRandomly();
	}
	
}