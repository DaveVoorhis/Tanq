package uk.ac.derby.Tanq.Core;

import java.util.*;

import uk.ac.derby.Animator2D.Movie;
import uk.ac.derby.GameEngine2D.Vector3D;
import uk.ac.derby.GameEngine2D.Arena;
import uk.ac.derby.GameEngine2D.SpriteStatic;
import uk.ac.derby.Tanq.Brains.Brain;
import uk.ac.derby.Tanq.Brains.Opponent;

public class Battleground extends Arena {
	
	private static final int highestTanqImageNumber = 8;

	private static final long serialVersionUID = 0;
	
	private Vector<Tanq> tanqs = new Vector<Tanq>();
	private Vector<Ammo> ammoDumps = new Vector<Ammo>();
	private int tanqSerialNumber = 0;
	
	/** Initialisation. */
	public void initialise() {
		new Ammo(this);
	}
	
	/** Add a Tanq to the battleground. */
	void addTanq(Tanq t) {
		synchronized (tanqs) {
			tanqs.add(t);
		}
	}
	
	/** Remove a Tanq from the battleground. */
	void removeTanq(Tanq t) {
		synchronized (tanqs) {
			tanqs.remove(t);
		}
	}
	
	/** Add a movable Ammo dump to the battleground. */
	void addAmmo(Ammo a) {
		synchronized (ammoDumps) {
			ammoDumps.add(a);
		}
	}
	
	/** Remove a movable Ammo dump from the battleground. */
	void removeAmmo(Ammo a) {
		synchronized (ammoDumps) {
			a.suicide();
			ammoDumps.remove(a);
		}
	}
	
	/** Create a Tanq with the given Brain and name and launch it. */
	public void addPlayer(Brain brain, String name) {
		tanqSerialNumber++;
		String robotImageName ="images/Robo" + (tanqSerialNumber % highestTanqImageNumber + 1); 
		new Tanq(this, brain, name + " (" + tanqSerialNumber + ")",
    			(new Movie(2))
    				.add(robotImageName + "_01.png")
    				.add(robotImageName + "_02.png"));		
	}
	
	/** Return all TanqS that collide with a given Sprite. */
	public Vector<Tanq> getCollider(SpriteStatic s) {
		Vector<Tanq> v = new Vector<Tanq>();
		synchronized (tanqs) {
			for (Tanq t: tanqs)
				if (s.isTouching(t))
					v.add(t);
		}
		return v;
	}
	
	/** Return a Tanq that collides with a given Bullet.  Null if no collision. */
	public Tanq getCollider(Bullet b) {
		synchronized (tanqs) {
			for (Tanq t: tanqs) {
				if (t == b.getOwner()) 		// can't be shot by own bullets
					continue;
				if (b.isTouching(t))
					return t;
			}
			return null;
		}
	}
	
	/** Return true if a Tanq collides with another Tanq.  Null if no collision. */
	public Tanq getCollider(Tanq me) {
		synchronized (tanqs) {
			for (Tanq enemy: tanqs) {
				if (enemy == me)			// I can't collide with myself
					continue;
				if (enemy.isTouching(me))
					return enemy;
			}
			return null;
		}
	}
	
	/** Return a Vector of displayable Tanq scores, in rank order. */
	public Vector<TanqRanked> getRanking() {
		Vector<TanqRanked> v = new Vector<TanqRanked>();
		synchronized (tanqs) {
			for (Tanq i: tanqs)
				v.add(new TanqRanked(i));
		}
		Collections.sort(v);
		return v;
	}
	
	/** Get opponents for a given Tanq. */
	public Opponent[] getOpponentsFor(Tanq me) {
		synchronized (tanqs) {
			Opponent[] opponents = new Opponent[tanqs.size() - 1];
			int index = 0;
			for (Tanq tanq: tanqs)
				if (tanq != me) 
					opponents[index++] = new Opponent(tanq);
			return opponents;
		}
	}
	
	/** Get information about available Ammo dumps.  Return an empty array if there is no ammo. */
	public AmmoDump[] getAmmoLocations() {
		synchronized (ammoDumps) {
			AmmoDump[] dumps = new AmmoDump[ammoDumps.size()];
			int index = 0;
			for (Ammo ammo: ammoDumps)
				dumps[index++] = new AmmoDump(ammo);
			return dumps;
		}
	}

	/** Return true if a given rectangular region is likely to be blocked by a Tanq. */
	public boolean isBlockedByTanq(int x, int y, int width, int height) {
		synchronized (tanqs) {
			for (Tanq t: tanqs) {
				if (t.isTouching(new Vector3D(x, y, 0), (int)Math.sqrt(width * width + height * height)))
					return true;
			}
		}
		return false;
	}
	
	/** Obtain a random location, of a given width and height, that is not
	 * occupied by unpassable terrain or a Tanq.  
	 * 
	 * The time it will take for this to succeed is unbounded, but statistically 
	 * unlikely to be undesirably slow unless free spaces in the terrain are very 
	 * rare.  This is best used in a setup phase, rather than during play.
	 */
	public Vector3D getRandomLocation(int width, int height) {
		while (true) {
			int x = (int)(Math.random() * (float)getWidth());
			int y = (int)(Math.random() * (float)getHeight());
			if (!isBlockedByTerrain(x, y, width, height) && !isBlockedByTanq(x, y, width, height))
				return new Vector3D(x, y, 0);
		}
	}
	
}
