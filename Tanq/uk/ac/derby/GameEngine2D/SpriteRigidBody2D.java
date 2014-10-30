package uk.ac.derby.GameEngine2D;

import javax.swing.*;
import uk.ac.derby.Animator2D.*;

/** A movable sprite based on a RigidBody2D model of movement. */
public abstract class SpriteRigidBody2D extends SpriteMobile {
	
    private RigidBody2D body = new RigidBody2D();
	
    /** Create a Sprite, given a Movie to display. */
    public SpriteRigidBody2D(String name, Movie movie, Arena panel, Vector3D location) {
    	super(name, movie, panel, location);
    	body.setPosition(location);
    }

    /** Create a Sprite, given a static image. */
    public SpriteRigidBody2D(String name, ImageIcon img, Arena panel, Vector3D location) {
    	this(name, (new Movie()).add(img), panel, location);
    }

    // Handle movement
    public void movement() {
    	if (body == null)
    		return;
    	Vector3D oldPosition = getLocation();
		body.updateBodyEuler(0.1);
		if (body.getX() > getArenaWidth() - getWidth() ||
		    body.getY() > getArenaHeight() - getHeight() ||
		    body.getX() < 0 ||
		    body.getY() < 0 ||
		    getArena().isBlockedByTerrain((int)body.getX(), (int)body.getY(), getWidth(), getHeight())) {
				body.setPosition(oldPosition);
				// Shut down all velocities, etc.
				body.reset();
				// Notify interested parties.
				blockedByTerrain();
		} 
		setLocation(body.getPosition());
		BlockageInfo blockage = isMovementBlocked();
		if (blockage != null) {
			body.reset();
			body.setPosition(oldPosition);
			setLocation(body.getPosition());
			// Notify interested parties
			movementIsBlocked(blockage);
		}
    }

    /** This method may be overridden to define additional movement constraints.
     * Return non-null if movement is blocked.  BlockageInfo may be used to communicate
     * information about the blockage to movementIsBlocked. */
    public BlockageInfo isMovementBlocked() {
    	return null;
    }
    
    /** Get 2D rigid body model underlying this Sprite. */
    public final RigidBody2D getBody() {
    	return body;
    }

    /** Override this to be told you are blocked by terrain. */
    public void blockedByTerrain() {}
    
    /** Override this to be told you are blocked by isMovementBlocked(). */
    public void movementIsBlocked(BlockageInfo information) {}
    
    /** Override this to think and act as an autonomous agent. */
    public abstract void compute();
    
}
