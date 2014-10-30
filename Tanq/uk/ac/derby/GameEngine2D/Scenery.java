package uk.ac.derby.GameEngine2D;

import javax.swing.ImageIcon;

import uk.ac.derby.Animator2D.Movie;

/** A static sprite that does not interact with the environment. */
public class Scenery extends SpriteStatic {

	/** Create a Scenery, given a Movie to display. */
    public Scenery(String name, Movie movie, Arena panel, Vector3D location) {
    	super(name, movie, panel, location);
    }
    
    /** Create a Scenery, given a static image. */
    public Scenery(String name, ImageIcon img, Arena panel, Vector3D location) {
    	super(name, img, panel, location);
    }
	
	public void compute() {
	}

	public int getBoundingRadius() {
		return 0;
	}

}
