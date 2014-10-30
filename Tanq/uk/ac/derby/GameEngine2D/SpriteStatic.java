package uk.ac.derby.GameEngine2D;

import javax.swing.*;
import java.awt.*;

import uk.ac.derby.Animator2D.*;

/** A positionable sprite, with intelligence. */
public abstract class SpriteStatic implements Sprite {

	public final int monitorDelayTimeMillis = 1000;	// calc stats every second
	
	private boolean started = false;
	
    private JLabel imgLabel; // the Component that contains the image
    private Arena arena;	 // the Arena that contains this Sprite        
    private Player player = null;	 // current image player
    
    // Sprite attributes
    private String name;
    
    // Stats
    private long cumulativeCalculationDuration = 0;
    private long lastCalculationDuration = 0;
    private long calculations = 0;
    private boolean computerRunning = true;
 
	// Launch calculation thread
    private void startComputer() {
    	final Thread computer = new Thread() {
    		public void run() {
    			try {
    	    		// Perform user-defined calculations
    				while (computerRunning) {
    					long startTime = System.currentTimeMillis();
    					compute();
    					lastCalculationDuration = System.currentTimeMillis() - startTime;
    					cumulativeCalculationDuration += lastCalculationDuration;
    					calculations++;
    					// The above runs flat-out, so voluntarily yield processing
    					yield();
    				}
    			} catch (Throwable t) {
    				computerRunning = false;
    				arena.computerFailure(SpriteStatic.this, t);
    			}
    		}
    	};
    	computer.start();
    }
    
    /** Get the radius of the sprite's bounding region.  For collision purposes, sprites
     * are considered to be round.
     */
    public abstract int getBoundingRadius();
    
    /** Return true if this SpriteStatic is in collision with a location bounded by
     * a specified radius.
	 * 
	 * An excellent intro to accurate collision detection is at
	 * http://www.gamasutra.com/features/20020118/vandenhuevel_01.htm
	 * 
	 * This is not particularly accurate, intended more for speed.
	 * 
     */
    public final boolean isTouching(Vector3D myLocation, int myRadius) {
		Vector3D testSpritePosition = getLocation();
		int aX = (int)testSpritePosition.getX();
		int aY = (int)testSpritePosition.getY();
		int bX = (int)myLocation.getX();
		int bY = (int)myLocation.getY();
		int deltaXSquared = aX - bX; // calc. delta X
		deltaXSquared *= deltaXSquared; // square delta X
		int deltaYSquared = aY - bY; // calc. delta Y
		deltaYSquared *= deltaYSquared; // square delta Y
		// Calculate the sum of the radii, then square it
		int sumRadiiSquared = getBoundingRadius() + myRadius;
		sumRadiiSquared *= sumRadiiSquared;
		return (deltaXSquared + deltaYSquared <= sumRadiiSquared);    	
    }
    
	/** Return true if this SpriteStatic is in collision with another
	 * SpriteStatic.
	 */
    public final boolean isTouching(SpriteStatic me) {
    	return isTouching(me.getLocation(), me.getBoundingRadius());
    }
   
    /** Get the graphics object that will be displayed in the Arena. */
    public Component getGraphicsObject() {
    	return imgLabel;
    }
    
    /** Obtain cumulative milliseconds spent calculating. */
    public long getCumulativeCalculationTime() {
    	return cumulativeCalculationDuration;
    }
    
    /** Obtain most duration in millseconds of most recent calculation. */
    public long getLastCalculationDuration() {
    	return lastCalculationDuration;
    }
    
    /** Obtain number of calculations to date. */
    public long getCalculationsCount() {
    	return calculations;
    }
    
    /** Return true if the calculator is running. */
    public boolean isComputerRunning() {
    	return computerRunning;
    }

    /** Create a Sprite, given a Movie to display. */
    public SpriteStatic(String name, Movie movie, Arena panel, Vector3D location) {
    	arena = panel;
    	// Configure sprite
    	imgLabel = new JLabel();
    	this.name = name;
    	imgLabel.setToolTipText(name);
    	imgLabel.setOpaque(false);
    	setMovie(movie);
    	panel.addSprite(this);
    	setLocation(location);
    }

    /** Start sprite computer. */
    public void start() {
    	startComputer();
    }

    /** Stop sprite computer. */
    public void stop() {
    	computerRunning = false;
    }
    
    /** Commit suicide. */
    public void suicide() {
    	stop();
    	getArena().removeSprite(this);
    }
    
    /** Create a Sprite, given a static image. */
    public SpriteStatic(String name, ImageIcon img, Arena panel, Vector3D location) {
    	this(name, (new Movie()).add(img), panel, location);
    }

    /** Get the Arena this sprite is in. */
    public Arena getArena() {
    	return arena;
    }
    
    /** Override this to think and act as an autonomous agent. */
    public abstract void compute();
    
    /** Update position and other attributes. */
    public void update() {
    	if (!started) {
    		start();
    		started = true;
    	}
    }
    
    /** Change movie. */
    public void setMovie(Movie movie) {
    	if (player != null)
    		player.requestTerminate();
    	player = new Player(movie) {
    		public void notifyChangeFrame(ImageIcon img) {
   				imgLabel.setIcon(img);
    		}
    	};
    	player.start();
    }
    
    /** Get current movie. */
    public Movie getMovie() {
    	if (player == null)
    		return null;
    	return player.getMovie();
    }
    
    public String getSpriteName() {
    	return name;
    }
    
    public final int getWidth() {
    	return imgLabel.getWidth();
    }
    
    public final int getHeight() {
    	return imgLabel.getHeight();
    }
    
    public void setLocation(Vector3D location) {
    	imgLabel.setLocation((int)location.getX(), (int)location.getY());
    }
    
    public Vector3D getLocation() {
    	return new Vector3D((float)imgLabel.getX(), (float)imgLabel.getY(), 0);
    }
    
    public final int getArenaWidth() {
    	return arena.getWidth();
    }
    
    public final int getArenaHeight() {
    	return arena.getHeight();
    }
}
