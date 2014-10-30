package uk.ac.derby.Tanq.Core;

import uk.ac.derby.GameEngine2D.*;

/** AmmoDump provides information about an Ammo dump. */
public class AmmoDump {
	
	private Ammo ammo;
	
	public AmmoDump(Ammo dump) {
		ammo = dump;
	}
	
	/** Return the number of ClipS in the Ammo dump. */
	public int getClipCount() {
		return ammo.getClipCount();
	}
	
	/** Return the number of BulletS per clip. */
	public int getClipSize() {
		return Clip.clipSize;
	}
	
	/** Return the location of the AmmoDump. */
	public Vector3D getLocation() {
		return ammo.getLocation();
	}
}
