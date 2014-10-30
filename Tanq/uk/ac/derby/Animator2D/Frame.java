package uk.ac.derby.Animator2D;

import javax.swing.*;

/** A frame in a Movie, which is a graphic and an associated intended duration
 * in milliseconds.  If the duration is -1, the display duration will
 * be determined by a default in Movie. */
public class Frame {

	private ImageIcon image;
	private int duration = -1;
	
	/** Create a Frame, given an image and a specified display duration.
	 * 
	 * @param img - frame image
	 * @param durationMillis - display duration in millis, if -1, use Movie default  
	 */
	public Frame(ImageIcon img, int durationMillis) {
		image = img;
		durationMillis = duration;
	}
	
	/** Create a Frame, given an image.  Duration is default for associated Movie.
	 * 
	 * @param img - frame image
	 * @param durationMillis - display duration in millis, if -1, use Movie default  
	 */
	public Frame(ImageIcon img) {
		this(img, -1);
	}
	
	/** Create a Frame, given an image and a specified display duration.
	 * 
	 * @param imgFileName - frame image
	 * @param durationMillis - display duration in millis, if -1, use Movie default  
	 */
	public Frame(String imgFileName, int durationMillis) {
		this(new ImageIcon(imgFileName), durationMillis);
	}
	
	/** Create a Frame, given an image.  Duration is default for associated Movie.
	 * 
	 * @param imgFileName - frame image
	 * @param durationMillis - display duration in millis, if -1, use Movie default  
	 */
	public Frame(String imgFileName) {
		this(new ImageIcon(imgFileName));
	}
	
	/** Obtain the image associated with this Frame. */
	public ImageIcon getImage() {
		return image;
	}
	
	/** Obtain the intended duration of this Frame.  -1 to use Movie default. */
	public int getDuration() {
		return duration;
	}
}
