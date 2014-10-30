package uk.ac.derby.Tanq.Core;

import uk.ac.derby.Animator2D.Movie;
import uk.ac.derby.GameEngine2D.SpriteRigidBody2D;
import uk.ac.derby.GameEngine2D.Vector3D;

public class Bullet extends SpriteRigidBody2D {

	private Tanq shooter;
	
    public static class BulletMovie extends Movie {
    	BulletMovie() {
    		add("images/Spark01.png");
    		add("images/Spark02.png");
    		add("images/Spark03.png");
    		add("images/Spark04.png");
    	}
    }
	
	private boolean hit = false;
	
	/** Create a bullet. */
	Bullet(Tanq gunOwner, Vector3D position, float aimDegrees) {
		super("Bullet", new BulletMovie(), gunOwner.getArena(), position);
		shooter = gunOwner;
		Vector3D appliedForce = Vector3D.vRotate2D((float)aimDegrees, new Vector3D(2, 0, 0));
		getBody().setAppliedForce(appliedForce);
		getBody().setInertia(1.0f);
		getBody().setMass(1.0f);
		getBody().setProjectedArea(1.0f);
	}
	
	/** Get the Tanq that owns this bullet. */
	public Tanq getOwner() {
		return shooter;
	}
	
	public void compute() {
		if (hit || shooter == null)
			return;
		Battleground battleground = (Battleground)shooter.getArena();
		Tanq target = battleground.getCollider(this);
		if (target != null) {
			hit = true;
			getBody().setAppliedForce(new Vector3D(0, 0, 0));
			target.notifyInjury(getOwner());
			getOwner().notifyScored(target);
			// When target is hit, explode
			Movie m = new Movie() {
				public void notifyEndMovie() {
					suicide();
				}
			};
			m.setRepeating(false);
			m.add("images/BulletStrike01.png");
			m.add("images/BulletStrike02.png");
			m.add("images/BulletStrike03.png");
			m.add("images/BulletStrike04.png");
			m.add("images/BulletStrike05.png");
			m.add("");
			setMovie(m);
		}
	}
	
	public int getBoundingRadius() {
		return 2;
	}
	
	public void blockedByTerrain() {
		if (hit)	// guard against doing this repeatedly
			return;
		hit = true;
		// When terrain is hit, explode
		Movie m = new Movie() {
			public void notifyEndMovie() {
				suicide();
			}
		};
		m.setRepeating(false);
		m.add("images/BulletExplode01.png");
		m.add("images/BulletExplode02.png");
		m.add("images/BulletExplode03.png");
		m.add("images/BulletExplode04.png");
		m.add("images/BulletExplode05.png");
		m.add("");
		setMovie(m);
	}

}
