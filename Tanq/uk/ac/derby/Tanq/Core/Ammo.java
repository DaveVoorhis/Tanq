package uk.ac.derby.Tanq.Core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.Timer;

import uk.ac.derby.Animator2D.Movie;
import uk.ac.derby.GameEngine2D.*;

/** An ammo dump.  It contains four clips. */
class Ammo extends SpriteStatic {
	
    static class AmmoLiveMovie extends Movie {
    	AmmoLiveMovie() {
    		add("images/Ammo01.png");
    		add("images/Ammo02.png");
    	}
    }
	
    static class AmmoDyingMovie extends Movie {
    	AmmoDyingMovie() {
    		add("images/Ammo03.png");
    		add("images/Ammo04.png");
    	}
    }
	
    private Battleground battleground;
    
    private Stack<Scenery> clips = new Stack<Scenery>();
    private Vector<Tanq> consumers = new Vector<Tanq>();
    
    private Timer relocationTimer;

    private static Movie liveMovie = new AmmoLiveMovie();
    private static Movie dyingMovie = new AmmoDyingMovie();
    
    private void setRelocateTimer() {
		Timer t = new Timer(15 * 1000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				relocate();
			}
		});
		t.setRepeats(false);
		t.start();
    }
    
	Ammo(Battleground bg) {
		super("Ammo", liveMovie, bg, bg.getRandomLocation(15, 15));
		battleground = bg;
		reset();
		battleground.addAmmo(this);
		// Every minute, relocate
		relocationTimer = new Timer((60 * 1000) - (15 * 1000), new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				setMovie(dyingMovie);
				setRelocateTimer();
			}
		});
		relocationTimer.setRepeats(true);
		relocationTimer.start();
	}

	private void reset() {
		Vector3D location = getLocation();
		clips.push(new Scenery("AmmoClip", new Bullet.BulletMovie(), battleground, 
				new Vector3D((float)location.getX() + 3, (float)location.getY() + 2, 0.0f)));
		clips.push(new Scenery("AmmoClip", new Bullet.BulletMovie(), battleground, 
				new Vector3D((float)location.getX() + 8, (float)location.getY() + 2, 0.0f)));
		clips.push(new Scenery("AmmoClip", new Bullet.BulletMovie(), battleground, 
				new Vector3D((float)location.getX() + 3, (float)location.getY() + 7, 0.0f)));
		clips.push(new Scenery("AmmoClip", new Bullet.BulletMovie(), battleground, 
				new Vector3D((float)location.getX() + 8, (float)location.getY() + 7, 0.0f)));				
	}
	
	private synchronized void relocate() {
		setMovie(liveMovie);
		while (clips.size() > 0)
			clips.pop().suicide();
		setLocation(battleground.getRandomLocation(15, 15));
		consumers.clear();
		reset();
		relocationTimer.restart();
	}
	
	public int getBoundingRadius() {
		return 7;
	}
	
	public int getClipCount() {
		return clips.size();
	}
	
	public void compute() {
		for (Tanq t: battleground.getCollider(this)) {
			if (!consumers.contains(t)) {
				t.reload(new Clip());
				consumers.add(t);
				clips.pop().suicide();
				if (clips.empty())
					break;
			}
		}
		if (clips.empty())
			relocate();
	}
	
}
