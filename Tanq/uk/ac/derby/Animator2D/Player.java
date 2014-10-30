package uk.ac.derby.Animator2D;

import javax.swing.*;
import java.util.*;
import uk.ac.derby.Logger.Log;

/** A Player is a special thread that iterates over a Movie, invoking
 * notifyChangeFrame() whenever it is time to change the image.
 * 
 * @author dave
 */
public abstract class Player extends Thread {
	
	private boolean running = true;
	private Movie movie;
	private int currentFrameNumber = 0;
	
	/** Create a Player.  A Player does NOT draw the frames!  It
	 * simply invokes notifyChangeFrame() in accordance with the
	 * intended frame rate.  As with a Thread, start the Player by
	 * invoking start(). */
	public Player(Movie m) {
		movie = m;
	}

	/** Get the Movie that's currently playing. */
	public Movie getMovie() {
		return movie;
	}
	
	/** Invoked internally.  Do not invoke!!! */
	public void run() {
		Vector<Frame> strip = movie.getStrip();
		// If only one frame in a repeating Movie, then display it and quit
		// without calling notifyEndMovie(), because it doesn't really end.
		if (strip.size() == 1 && movie.isRepeating())
			notifyChangeFrame(((Frame)strip.get(0)).getImage());
		else {
			if (strip.size() > 0) {
				try {
					while (running) {
						if (currentFrameNumber < strip.size()) {
							Frame frame = (Frame)strip.get(currentFrameNumber++);
							notifyChangeFrame(frame.getImage());
							if (frame.getDuration() >= 0)
								sleep(frame.getDuration());
							else
								sleep(movie.getDefaultFrameDuration());
						} else if (movie.isRepeating())
							currentFrameNumber = 0;
						else
							running = false;
					}
				} catch (Throwable t) {
					Log.println("Player: movie terminated: " + t.toString());
					running = false;
				}
			}
			movie.notifyEndMovie();
		}
	}
	
	/** The user may call this to terminate the Movie. */
	public void requestTerminate() {
		running = false;
	}
	
	/** Invoked by Player whenever a frame should change.  Display
	 * mechanisms should present the image specified by the 'image'
	 * parameter. */
	public abstract void notifyChangeFrame(ImageIcon image);
}
