package uk.ac.derby.Tanq.Core;

/** A Clip holds a number of BulletS.  An Ammo holds a number of ClipS. */
public class Clip {
	
	public static final int clipSize = 20;

	Clip() {}

	int getBulletsAvailable() {
		return clipSize;
	}
	
}
