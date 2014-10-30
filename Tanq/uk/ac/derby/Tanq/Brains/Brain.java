package uk.ac.derby.Tanq.Brains;

import uk.ac.derby.Animator2D.*;
import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Logger.*;
import uk.ac.derby.Tanq.Core.*;

import java.util.*;

/** A Tanq brain. */
public abstract class Brain {

	private Tanq tanq;
	private boolean locked = false;
	private Vector<BrainListener> listeners = new Vector<BrainListener>();	
	private boolean debugging = false;
	
	/** Create an instance of a Brain. */
	public Brain() {
	}

	/** Set debugging mode. */
	public void setDebugging(boolean debug) {
		debugging = debug;
	}
	
	/** Return true if in debugging mode. */
	public boolean isDebugging() {
		return debugging;
	}
	
	/** This method is invoked once when the Brain is loaded.  It may be used to
	 * configure the Brain and launch ThreadS that interact with the brain.
	 */
	public void initialise() {}
	
	/** This method is invoked repeatedly and continuously during the life of a Tanq,
	 * and may be used to implement any desired computations.  Brain computations
	 * may also be performed by ThreadS that interact with the Brain.
	 */
	public void compute() {}
	
	/** Advise this Brain it belongs to a Tanq.  This will be invoked internally,
	 * and should never be called by user processes. */
	public final void setTanq(Tanq t) {
		if (locked)
			return;
		tanq = t;
		locked = true;
	}

	/** Get the name of the Tanq. */
	public final String getName() {
		return tanq.getSpriteName();
	}
	
	/** Set the throttle position. */
	public final void setThrottle(float f) {
		tanq.getBody().setThrust(f);
	}
	
	/** Set steering forces. */
	public final void setSteering(boolean left, boolean right) {
		tanq.getBody().setSteeringThrusters(left, right);
	}

	/** Set a directional force. */
	public final void setAppliedForce(Vector3D force) {
		tanq.getBody().setAppliedForce(force);
	}

	/** Shortcut for applying a directional force.  Angle in degrees, velocity in units. */
	public final void setDirection(double directionInDegrees, double velocity) {
		setAppliedForce(Vector3D.vRotate2D((float)directionInDegrees, 
				new Vector3D((float)velocity, 0, 0)));
	}
	
	/** Obtain body radius. */
	public final int getBoundingRadius() {
		return tanq.getBoundingRadius();
	}
	
	/** Obtain velocity. */
	public final float getVelocity() {
		return tanq.getBody().getVelocity();
	}
	
	/** Obtain velocity as a Vector. */
	public final Vector3D getVelocityVector() {
		// Return copy to prevent modification
		return new Vector3D(tanq.getBody().getVelocityVector());
	}
	
	/** Obtain location. */
	public final Vector3D getLocation() {
		// Return copy to prevent modification
		return new Vector3D(tanq.getBody().getPosition());
	}
	
	/** Set location. */
	public final void setLocation(Vector3D location) {
		tanq.getBody().setPosition(location);
	}

	/** Obtain orientation. */
	public final float getOrientationInDegrees() {
		return tanq.getBody().getOrientationInDegrees();
	}
	
	/** Obtain the number of milliseconds it takes to reload the gun. */
	public final int getGunReloadDurationMillis() {
		return tanq.getGun().getReloadDurationMillis();
	}
	
	/** How many degrees does an unfired gun cool by per second. */
	public final int getGunHeatDecreasePerSecond() {
		return tanq.getGun().getHeatDecreasePerSecond();
	}
	
	/** How many degrees does a fired gun increase by per shot. */
	public final int getGunHeatIncreasePerShot() {
		return tanq.getGun().getHeatIncreasePerShot();
	}
	
	/** At what temperature does the gun overheat and jam. */
	public final int getGunOverheatTemp() {
		return tanq.getGun().getOverheatTemp();
	}
	
	/** What must jammed gun cool down to before it can fire again. */ 
	public final int getGunUnjamTemp() {
		return tanq.getGun().getUnjamTemp();
	}
	
	/** What is the ambient gun temperature. */
	public final int getGunAmbientTemp() {
		return tanq.getGun().getAmbientTemp();
	}
	
	/** True if the gun is loaded.  If it is loaded and unjammed, it can fire. */
	public final boolean isGunLoaded() {
		return tanq.getGun().isLoaded();
	}
	
	/** True if the gun is not jammed.  If it is loaded and unjammed, it can fire. */
	public final boolean isGunJammed() {
		return tanq.getGun().isJammed();
	}
	
	/** Get current gun temperature. */
	public final int getGunCurrentTemp() {
		return tanq.getGun().getCurrentTemp();
	}
	
	/** Get number of BulletS remaining in gun. */
	public final int getGunBulletsAvailable() {
		return tanq.getGun().getBulletsAvailable();
	}
	
	/** Fire the gun in a given direction. */
	public final void fire(float directionInDegrees) {
		tanq.fire(directionInDegrees);
	}
	
	/** Invoke to notify when hit by a bullet. */
	public final void doNotifyInjury(Opponent shooter) {
		notifyInjury(shooter);
		for (BrainListener i: listeners)
			i.notifyInjury(shooter);
	}
	
	/** Invoke to notify when an opponent is hit by one of our bullets. */
	public final void doNotifyScored(Opponent victim) {
		notifyScored(victim);
		for (BrainListener i: listeners)
			i.notifyScored(victim);
	}

	/** Invoke to notify when we bump into an opponent, or an opponent bumps into us. */
	public final void doNotifyBumped(Opponent opponent) {
		notifyBumped(opponent);
		for (BrainListener i: listeners)
			i.notifyBumped(opponent);
	}
	
	/** Invoke to notify when we bump into terrain or arena edges. */
	public final void doNotifyBlockedByTerrain() {
		notifyBlockedByTerrain();
		for (BrainListener i: listeners)
			i.notifyBlockedByTerrain();
	}

	/** Invoked when hit by a bullet. */
	public void notifyInjury(Opponent shooter) {}
	
	/** Invoked when an opponent is hit by one of our bullets. */
	public void notifyScored(Opponent victim) {}

	/** Invoked when we bump into an opponent, or an opponent bumps into us. */
	public void notifyBumped(Opponent opponent) {}
	
	/** Invoked when we bump into terrain or arena edges. */
	public void notifyBlockedByTerrain() {}

	/** Register to receive notifications of Brain events.  This is useful
	 * if you aren't a Brain yourself, but would like to be notified of
	 * injuries, terrain blockage, scores, being bumped by an opponent, etc.
	 */
	public final void addListener(BrainListener b) {
		listeners.add(b);
	}
	
	/** Unregister to receive notifications of Brain events. */
	public final void removeListener(BrainListener b) {
		listeners.remove(b);
	}
	
	/** Number of times our bullets have hit an opponent. */
	public final int getScore() {
		return tanq.getScore();
	}
	
	/** Number of times an opponent's bullet has hit us. */
	public final int getInjuries() {
		return tanq.getInjuries();
	}
	
	/** Number of times we've collided with an opponent. */
	public final int getCollisions() {
		return tanq.getCollisions();
	}

	/** Number of times we've bumped into unpassable terrain. */
	public final int getBumps() {
		return tanq.getBumps();
	}

	/** Return the landscape width in pixels. */
	public final int getLandscapeWidth() {
		return tanq.getArena().getLandscapeWidth();
	}
	
	/** Return the landscape height in pixels. */
	public final int getLandscapeHeight() {
		return tanq.getArena().getLandscapeHeight();
	}
	
	/** Get a landscape map of a given scale.
	 *
	 * @param scale - 1.0 = 1:1 scale, 0.5 = 2:1 scale, 0.25 = 4:1 scale, etc.
	 */
	public final uk.ac.derby.GameEngine2D.Map getMap(float scale) {
		return tanq.getArena().getMap(scale);
	}
	
	/** Get a blurred landscape map of a given scale.  A blurred map has a one
	 * map pixel semi-unpassable buffer around each map pixel.  This helps prevent
	 * objects from running into terrain.
	 *
	 * @param scale - 1.0 = 1:1 scale, 0.5 = 2:1 scale, 0.25 = 4:1 scale, etc.
	 * @param blur = blur extent -- 0 none, 1 one map pixel, 2 two map pixels, etc.
	 */
	public final uk.ac.derby.GameEngine2D.Map getBlurredMap(float scale, int blur) {
		return tanq.getArena().getBlurredMap(scale, blur);
	}
	
	/** Obtain an array of opponent names and locations. */
	public final Opponent[] getOpponents() {
		return tanq.getOpponents();
	}
	
	/** Obtain an array of information about Ammo dumps. 
	 * 
	 * Each Tanq (or, more accurately, its gun) now carries a limited supply of bullets.  When
	 * newly created, a Tanq has Clip.clipSize bullets.
	 * 
	 * An ammo dump contains four clips of Clip.clipSize bullets each.  When a Tanq runs over an
	 * ammo dump, it is loaded with one clip.  A given Tanq may load at a given ammo dump once!
	 * 
	 * When an ammo dump has been emptied of clips, a new ammo dump magically appears in a new
	 * location.
	 */
	public final AmmoDump[] getAmmoDumps() {
		return tanq.getAmmoLocations();
	}
	
	/** Obtain a random location sufficient to hold the Tanq. */
	public final Vector3D getRandomLocation() {
		int wAndh = getBoundingRadius() * 2;
		return tanq.getBattleground().getRandomLocation(wAndh, wAndh);
	}
	
	/** Send a line to whatever subsystem displays messages. */
	public final void println(String s) {
		Log.println(getName() + " says: " + s);
	}
	
	/** Put a graphical marker on the Arena.  Useful for debugging. */
	public final SpriteStatic markPoint(Vector3D position) {
		Movie markerMovie = new Movie();
		markerMovie.add("images/Marker.png");
		return new Scenery("x", markerMovie, tanq.getArena(), position) {
			public int getBoundingRadius() {
				return 5;
			}
		};
	}
}
