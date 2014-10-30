package uk.ac.derby.Tanq.Core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import uk.ac.derby.GameEngine2D.Vector3D;

public class Gun {
    public final int reloadDuration = 2000;		// reload time in milliseconds
    public final int heatDecreasePerSecond = 5;	// cooldown rate
    public final int heatIncreasePerShot = 20;	// heatup rate
    public final int overheatTemp = 200;		// over this temperature, gun jams until it cools
    public final int unjamTemp = 110;			// cool down to this, and overheated gun unjams
    public final int ambientTemp = 20;			// gun starts at this temperature

    private boolean loaded = true;
    private boolean jammed = false;
    private int currentTemp = ambientTemp;
    private int bullets = 0;
    
	private Timer reload = new Timer(reloadDuration, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			loaded = true;
		}
	});
	
	private Timer cooldown = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			if (currentTemp == ambientTemp)
				return;
			if (currentTemp > ambientTemp) {
				currentTemp -= heatDecreasePerSecond;
				if (currentTemp < ambientTemp)
					currentTemp = ambientTemp;
			}
			if (currentTemp < unjamTemp)
				jammed = false;
		}
	});
	
	public int getReloadDurationMillis() {
		return reloadDuration;
	}
	
	public int getHeatDecreasePerSecond() {
		return heatDecreasePerSecond;
	}
	
	public int getHeatIncreasePerShot() {
		return heatIncreasePerShot;
	}
	
	public int getOverheatTemp() {
		return overheatTemp;
	}
	
	public int getUnjamTemp() {
		return unjamTemp;
	}
	
	public int getAmbientTemp() {
		return ambientTemp;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public boolean isJammed() {
		return jammed;
	}
	
	public int getCurrentTemp() {
		return currentTemp;
	}

	public int getBulletsAvailable() {
		return bullets;
	}
	
	Gun() {
		reload.setRepeats(false);
		cooldown.setRepeats(true);
	}
	
	/** Load a fresh clip. */
	void reload(Clip clip) {
		bullets = clip.getBulletsAvailable();
	}
	
	public void fire(Tanq owner, Vector3D gunLocation, float directionInDegrees) {
		synchronized (this) {
			if (loaded && !jammed && bullets > 0) {
				new Bullet(owner, gunLocation, directionInDegrees);
				bullets--;
				currentTemp += heatIncreasePerShot;
				if (currentTemp > overheatTemp)
					jammed = true;
				loaded = false;
				reload.start();
				cooldown.restart();
			}
		}
	}

}
