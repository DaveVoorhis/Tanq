package uk.ac.derby.GameEngine2D;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
import uk.ac.derby.Logger.Log;

/** An Arena is a playground for SpriteS.  It extends JLayeredPane. */
public class Arena extends JLayeredPane {
	
	private static final long serialVersionUID = 0;
	private ImageIcon background = null;
	private int[] backgroundPixels = null;
	private Vector<Sprite> sprites = new Vector<Sprite>();
	private HashMap<Float, Map> normalMaps = new HashMap<Float, Map>();
	private HashMap<Float, Map> blurredMaps = new HashMap<Float, Map>();
	
	/** Create an arena. */
	public Arena() {
		setLayout(new UserLayout());
	    setOpaque(true);
	    setDoubleBuffered(true);
	}
	
	/** Get a Map, given a map scale.  MapS are cached for performance. */
	public Map getMap(float scale) {
		Float theScale = new Float(scale);
		Map map = normalMaps.get(theScale);
		if (map == null) {
			map = new Map(scale, this, 0);
			normalMaps.put(theScale, map);
		}
		return map;
	}
	
	/** Get a blurred Map, given a map scale.  MapS are cached for performance.  A blurred map has
	 * a one pixel buffer surrrounding each landscape pixel.
	 */
	public Map getBlurredMap(float scale, int blur) {
		Float theScale = new Float(scale);
		Map map = blurredMaps.get(theScale);
		if (map == null) {
			map = new Map(scale, this, blur);
			blurredMaps.put(theScale, map);
		}
		return map;
	}
	
	/* Set the landscape map.  From it, a matrix, backgroundPixels[w * h], is created
	 * to indicate the colour of each background pixel.  Currently, a pure white
	 * pixel indicates passable territory.  Anything else is unpassable.
	 * 
	 * backgroundPixels may be interpreted as follows:
	 * 
	 *	for (int j = 0; j < height; j++) {
	 *	    for (int i = 0; i < width; i++) {
	 *	    	int pixel = backgroundPixels[j * w + i];
	 *			int alpha = (pixel >> 24) & 0xff;
	 *			int red   = (pixel >> 16) & 0xff;
	 *			int green = (pixel >>  8) & 0xff;
	 *			int blue  = (pixel      ) & 0xff;
	 *	    }
	 *	}
	 */
	public void setBackground(String imageName) {
		background = new ImageIcon(imageName);
		int width = background.getIconWidth();
		int height = background.getIconHeight();
	    setMinimumSize(new Dimension(width, height));
	    setMaximumSize(new Dimension(width, height));
	    setBounds(0, 0, width, height);
	    JLabel label = new JLabel();
	    label.setIcon(background);
	    add(label);
	    constructLandscapeMap();
	}

	/** Return landscape width in pixels. */
	public int getLandscapeWidth() {
		return background.getIconWidth();
	}
	
	/** Return landscape height in pixels. */
	public int getLandscapeHeight() {
		return background.getIconHeight();
	}
	
	/* Construct the landscape map.  
	 * @see setBackground
	 */
	private void constructLandscapeMap() {
		int w = background.getIconWidth();
		int h = background.getIconHeight();
		if (w < 0 || h < 0)
			throw new Violation("Arena: Unable to create landscape map because background image could not be loaded.");
		Log.println("Arena: Constructing " + w + " x " + h + " landscape map.");
		backgroundPixels = new int[w * h];
		PixelGrabber pg = new PixelGrabber(background.getImage(), 0, 0, w, h, backgroundPixels, 0, w);
		try {
		    pg.grabPixels();
		} catch (InterruptedException e) {
		    Log.println("Arena: Interrupted waiting for pixels!");
		    return;
		}
		int status = pg.getStatus();
		if ((status & ImageObserver.ABORT) != 0) {
		    Log.println("Arena: Error in image fetch: Status=" + status);
		    if ((status & ImageObserver.ABORT) != 0)
		    	Log.println("Arena: ImageObserver.ABORT");
		    if ((status & ImageObserver.ALLBITS) != 0)
		    	Log.println("Arena: ImageObserver.ALLBITS");
		    if ((status & ImageObserver.ERROR) != 0)
		    	Log.println("Arena: ImageObserver.ERROR");
		    if ((status & ImageObserver.FRAMEBITS) != 0)
		    	Log.println("Arena: ImageObserver.FRAMEBITS");
		    if ((status & ImageObserver.HEIGHT) != 0)
		    	Log.println("Arena: ImageObserver.HEIGHT");
		    if ((status & ImageObserver.PROPERTIES) != 0)
		    	Log.println("Arena: ImageObserver.PROPERTIES");
		    if ((status & ImageObserver.SOMEBITS) != 0)
		    	Log.println("Arena: ImageObserver.SOMEBITS");
		    if ((status & ImageObserver.WIDTH) != 0)
		    	Log.println("Arena: ImageObserver.WIDTH");
		    return;
		}
	}
	
	/** Return true if a given pixel location on the map is occupied
	 * by unpassable terrain.  Currently this is indicated by any non-white
	 * pixel on the background image.
	 */
	public final boolean isBlockedByTerrain(int x, int y) {
    	return (backgroundPixels[y * background.getIconWidth() + x] != -1);		
	}
	
	/** Return true if a block of pixel locations on the map are occupied
	 * by unpassable terrain, as above.
	 */
	public final boolean isBlockedByTerrain(int x, int y, int width, int height) {
		int w = background.getIconWidth();
		for (int j = y; j < height + y; j++)
			for (int i = x; i < width + x; i++)
				if (backgroundPixels[j * w + i] != -1)
					return true;
		return false;
	}
	
	/** Add a Sprite to this arena.  Invoked by the Sprite's constructor. */
	void addSprite(Sprite sprite) {
		synchronized (this) {
			sprites.add(sprite);
			Component graphic = sprite.getGraphicsObject();
			add(graphic);		// add Sprite's graphic
			moveToFront(graphic);
		}
	}
	
	/** Remove a Sprite from this arena.  Invoked when a Sprite dies. */
	void removeSprite(Sprite sprite) {
		synchronized (this) {
			sprites.remove(sprite);
			remove(sprite.getGraphicsObject());
			repaint();
		}
	}
	
	/** Receive notification from a Sprite of a computer failure. */
	void computerFailure(Sprite notifier, Throwable t) {
		Log.println(notifier.getSpriteName() + ": Computer says 'No.': " + t.toString());
		t.printStackTrace();
	}
	
	/** Receive notification from a Sprite of a motor failure. */
	void motorFailure(Sprite notifier, Throwable t) {
		Log.println(notifier.getSpriteName() + ": Motor failure: " + t.toString());
		t.printStackTrace();
	}

	/** Start up sprite processing. */
	public void start() {
		Thread t = new Thread() {
			public void run() {
				Utility.delay(1000);
				while (true) {
					Sprite[] spriteArray = (Sprite[])sprites.toArray(new Sprite[0]);
					long duration = 0;
					for (int i=0; i<spriteArray.length; i++) {
						long millisStart = System.currentTimeMillis();
						spriteArray[i].update();
						duration += System.currentTimeMillis() - millisStart;
					}
					if (duration < 5)
						Utility.delay(5 - duration);
				}
			}
		};
		t.start();
		initialise();
	}
	
	/** Override to implement custom initialisation. */
	public void initialise() {}
	
}
