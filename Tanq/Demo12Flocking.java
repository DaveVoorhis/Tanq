import sampleBrains.Flocker;
import uk.ac.derby.GameEngine2D.Vector3D;
import uk.ac.derby.Tanq.GUI;
import uk.ac.derby.Tanq.Brains.*;
import uk.ac.derby.Tanq.Navigation.Driver;

/** Flocking demo. */
public class Demo12Flocking {

	// A Brain that follows a specified pattern
	static class PatternPathDriver extends Brain {
		public void initialise() {
			Driver driver = new Driver(this) {
				private void fillpath() {
					addToPath(new Vector3D(100f, 100f, 0f));
					addToPath(new Vector3D(250f, 100f, 0f));
					addToPath(new Vector3D(500f, 100f, 0f));
					addToPath(new Vector3D(500f, 250f, 0f));
					addToPath(new Vector3D(500f, 500f, 0f));
					addToPath(new Vector3D(500f, 100f, 0f));
					addToPath(new Vector3D((float)Math.random() * 400 + 100, (float)Math.random() * 400 + 100, 0));
					addToPath(new Vector3D(100f, 500f, 0f));					
				}
				// Fill path initially
				public void notifyReachedDestination() {
					fillpath();
				}
				// Keep path filled at all times, so we can control speed around corners
				public void notifyNearingDestination() {
					fillpath();
				}
			};
			driver.setSpeedLimit(6.0f);
			driver.start();
		}
	};

	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/BackgroundCompetition01.png");
		
		Brain leader = new PatternPathDriver();
		GUI.addPlayer(leader, "Leader");
		
		for (int i=0; i<5; i++)
			GUI.addPlayer(new Flocker(leader), "Flocker" + i);
	}
}
