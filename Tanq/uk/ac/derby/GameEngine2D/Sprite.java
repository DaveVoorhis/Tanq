package uk.ac.derby.GameEngine2D;

import java.awt.Component;

/** Generic definition of a Sprite, an object in a 2D space, made to
 * live in an Arena. */
public interface Sprite {
    
	/** Get the sprite's name. */
	public String getSpriteName();
		
    /** Get the graphics object that will be displayed in the Arena. */
    public Component getGraphicsObject();

    /** Launch threads and concurrent operations. */
    void start();

    /** Perform movement update. */
    public void update();
    
}
