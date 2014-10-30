package uk.ac.derby.Animator2D;

import java.util.*;
import javax.swing.*;

/** A Movie.  By default, the movie repeats. */
public class Movie {
	
	private Vector<Frame> strip = new Vector<Frame>();				// Collection of FrameS.
	private int frameDuration;							// Default frame duration in millis
	private boolean repeating = true;
	
	/** Get the film strip.  Used by Player. */
	final Vector<Frame> getStrip() {
		return strip;
	}
	
	/** Constructor, with specified default Frame rate in FPS. */
	public Movie(int framesPerSecond) {
		setFramerate(framesPerSecond);
	}
	
	/** Constructor, using default Frame rate of ~8fps. */
	public Movie() {
		this(8);
	}
	
	public void setFramerate(int framesPerSecond) {
		frameDuration = 1000 / framesPerSecond;		
	}
	
	/** Set repeating on/off.  If off (default), display stops on last image.  
	 * Returns this Movie so calls can be chained. 
	 */
	public Movie setRepeating(boolean flag) {
		repeating = flag;
		return this;
	}
	
	/** Return true if isRepeating */
	public boolean isRepeating() {
		return repeating;
	}
	
	/** Get default frame duration in millis.  Used if a Frame's duration is -1. */
	public int getDefaultFrameDuration() {
		return frameDuration;
	}
	
	/** Add a Frame.  Returns this Movie so calls to add() can be chained. */
	public Movie add(Frame f) {
		strip.add(f);
		return this;
	}
	
	/** Add a Frame.  Returns this Movie so calls to add() can be chained.
	 * With specified display duration in milliseconds. 
	 */
	public Movie add(ImageIcon image, int durationMillis) {
		return add(new Frame(image, durationMillis));
	}
	
	/** Obtain the first frame as an ImageIcon */
	public ImageIcon getIcon() {
		return strip.get(0).getImage();
	}
	
	/** Add a Frame.  Returns this Movie so calls to add() can be chained.
	 * Duration will be determined by Movie. 
	 */
	public Movie add(ImageIcon image) {
		return add(new Frame(image));
	}

	/** Add a Frame.  Returns this Movie so calls to add() can be chained.
	 * With specified display duration in milliseconds. 
	 */
	public Movie add(String imageFileName, int durationMillis) {
		return add(new Frame(imageFileName, durationMillis));
	}
	
	/** Add a Frame.  Returns this Movie so calls to add() can be chained.
	 * Duration will be determined by Movie. 
	 */
	public Movie add(String imageFileName) {
		return add(new Frame(imageFileName));
	}
	
	/** Invoked by Player when movie ends.  Override to be notified
	 * that the movie ends.  If the movie repeats, this will only be 
	 * invoked if the user invokes requestTerminate().
	 */
	public void notifyEndMovie() {}

}
