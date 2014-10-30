package uk.ac.derby.Tanq.Core;

import uk.ac.derby.Animator2D.Movie;
import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.Brains.Brain;
import uk.ac.derby.Tanq.Brains.Opponent;

import javax.swing.*;

public class Tanq extends SpriteRigidBody2D {
	
	private Movie zapMovie;
	private Movie mainMovie;
	private Movie preSelectedMovie;
	private Movie selectedMovie;
	private Gun gun = new Gun();
	private Brain brain;
	private Battleground battleground;
	private boolean selected = false;
	private int score = 0;
	private int injuries = 0;
	private int collisions = 0;
	private int bumps = 0;
	
	Tanq(Battleground theBattleground, Brain theBrain, String name, Movie movie) {
		super(name, movie, theBattleground, theBattleground.getRandomLocation(15, 15));
		zapMovie = new Movie() {
			public void notifyEndMovie() {
				setMovie(mainMovie);
				mainMovie = null;
			}
		};
		zapMovie.add("images/RoboZap_01.png");
		zapMovie.add("images/RoboZap_02.png");
		zapMovie.add("images/RoboZap_03.png");
		zapMovie.add("images/RoboZap_04.png");
		zapMovie.setRepeating(false);
		selectedMovie = new Movie();
		selectedMovie.add("images/RoboSelected_01.png");
		selectedMovie.add("images/RoboSelected_02.png");
		battleground = theBattleground;
		brain = theBrain;
		battleground.addTanq(this);
		brain.setTanq(this);
		// Initial clip
		reload(new Clip());
		// Initialise concurrently, so slow BrainS don't cause trouble.
		(new Thread() {
			public void run() {
				brain.initialise();				
			}
		}).start();		
	}

	/** Get the battleground. */
	public Battleground getBattleground() {
		return battleground;
	}
	
	/** Get opponents for this Tanq. */
	public Opponent[] getOpponents() {
		return battleground.getOpponentsFor(this);
	}
	
	/** Get Ammo locations for this Tanq. */
	public AmmoDump[] getAmmoLocations() {
		return battleground.getAmmoLocations();
	}
	
	/** Set or unset selected status. */
	public void setSelected(boolean b) {
		if (b == selected)
			return;
		selected = b;
		if (selected) {
			preSelectedMovie = getMovie();
			setMovie(selectedMovie);
		} else {
			setMovie(preSelectedMovie);
		}
	}
	
	/** Return true if selected. */
	public boolean isSelected() {
		return selected;
	}
	
	/** Get the gun. */
	public Gun getGun() {
		return gun;
	}
	
	/** Called when hit by a bullet belonging to shooter. */
	public void notifyInjury(Tanq shooter) {
		injuries++;
		brain.doNotifyInjury(new Opponent(shooter));
		if (mainMovie == null) {
			mainMovie = getMovie();
			setMovie(zapMovie);
		}
	}
	
	/** Called when a bullet belonging to this strikes another Tanq. */
	public void notifyScored(Tanq victim) {
		score++;
		brain.doNotifyScored(new Opponent(victim));
	}
	
	public int getBoundingRadius() {
		return 7;
	}
	
	/** Reload the gun with a Clip. */
	public void reload(Clip clip) {
		gun.reload(clip);
	}
	
	/** Fire the gun in a given direction. */
	public void fire(float directionInDegrees) {
		gun.fire(this, getLocation().add(new Vector3D(getWidth() / 2, getHeight() / 2, 0)), 
				 directionInDegrees);		
	}

	// Used to communicate between isMovementBlocked() and movementIsBlocked().
	private class Blockage implements BlockageInfo {
		private Tanq bumpedInto;
		public Blockage(Tanq bumped) {
			bumpedInto = bumped;
		}
		public Tanq getBumpedTanq() {
			return bumpedInto;
		}
	}
	
	/** This prevents TanqS from running over each other. */
	public BlockageInfo isMovementBlocked() {
		Tanq bumpedInto = battleground.getCollider(this);
		if (bumpedInto == null)
			return null;
		return new Blockage(bumpedInto);
	}
    
    /** Override this to be told you are blocked by movementBlocked(). */
    public void movementIsBlocked(BlockageInfo blockage) {
    	Tanq bumpedInto = ((Blockage)blockage).getBumpedTanq();
		collisions++;
		bumpedInto.collisions++;
		brain.doNotifyBumped(new Opponent(bumpedInto));
		bumpedInto.brain.doNotifyBumped(new Opponent(this));    	
    }
	
    /** Override this to be told you are blocked by terrain. */
    public void blockedByTerrain() {
    	bumps++;
    	brain.doNotifyBlockedByTerrain();
    }
	
	public void compute() {
		brain.compute();
	}

	public int getScore() {
		return score;
	}
	
	public int getInjuries() {
		return injuries;
	}
	
	public int getCollisions() {
		return collisions;
	}

	public int getBumps() {
		return bumps;
	}
	
	public int getBulletsAvailable() {
		return gun.getBulletsAvailable();
	}
	
	public ImageIcon getImage() {
		return getMovie().getIcon();
	}
	
}
