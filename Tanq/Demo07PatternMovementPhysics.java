import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.GUI;
import uk.ac.derby.Tanq.Brains.*;
import uk.ac.derby.Tanq.Navigation.Driver;

// Pattern movement.
public class Demo07PatternMovementPhysics {
	
	// A Brain that moves a Tanq in a rectangular pattern
	static class SquarePathDriver extends Brain {
		public void initialise() {
			Driver driver = new Driver(this) {
				private void fillpath() {
					addToPath(new Vector3D(100f, 100f, 0f));
					addToPath(new Vector3D(500f, 100f, 0f));
					addToPath(new Vector3D(500f, 500f, 0f));
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
	
	/**
	 * An example of basic pattern movement.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/Background05.png");

		GUI.addPlayer(new SquarePathDriver(), "PatternMoverPhysics");
	}
}
