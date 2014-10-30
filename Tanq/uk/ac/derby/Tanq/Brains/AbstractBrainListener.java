package uk.ac.derby.Tanq.Brains;

/** This class may be used to extend classes that listen to Brain events. */
public class AbstractBrainListener implements BrainListener {
	
	/** Invoked when hit by a bullet. */
	public void notifyInjury(Opponent shooter) {}
	
	/** Invoked when an opponent is hit by one of our bullets. */
	public void notifyScored(Opponent victim) {}

	/** Invoked when we bump into an opponent, or an opponent bumps into us. */
	public void notifyBumped(Opponent opponent) {}
	
	/** Invoked when we bump into terrain or arena edges. */
	public void notifyBlockedByTerrain() {}

}
