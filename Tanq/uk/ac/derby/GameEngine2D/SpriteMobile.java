package uk.ac.derby.GameEngine2D;

import javax.swing.*;

import uk.ac.derby.Animator2D.*;

/** A Sprite that is in charge of its own movement. */
public abstract class SpriteMobile extends SpriteStatic {
	
    private boolean motorRunning = true;

    // Override to process movement.  This is the core of the motor.
    public abstract void movement();
    
	// Update position.  Invoked by a thread in Arena.
    public void update() {
    	super.update();
		try {
			if (motorRunning)
				movement();
		} catch (Throwable t) {
			motorRunning = false;
    		getArena().motorFailure(SpriteMobile.this, t);    				
		}
    }
    
    /** Stop motor. */
    public void stop() {
    	super.stop();
    	motorRunning = false;
    }
    
    /** Return true if the motor is running. */
    public boolean isMotorRunning() {
    	return motorRunning;
    }
	
    /** Create a Sprite, given a Movie to display. */
    public SpriteMobile(String name, Movie movie, Arena panel, Vector3D location) {
    	super(name, movie, panel, location);
    }

    /** Create a Sprite, given a static image. */
    public SpriteMobile(String name, ImageIcon img, Arena panel, Vector3D location) {
    	this(name, (new Movie()).add(img), panel, location);
    }

    /** Override this to think and act as an autonomous agent. */
    public abstract void compute();
}
