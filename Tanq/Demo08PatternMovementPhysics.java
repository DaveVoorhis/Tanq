import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.GUI;
import uk.ac.derby.Tanq.Brains.*;
import uk.ac.derby.Tanq.Navigation.Driver;

// Pattern movement.
public class Demo08PatternMovementPhysics {
	
	// A Driver that follows a random path.
	static class RandomPathDriver extends Brain {
		public void initialise() {
			Driver driver = new Driver(this) {
				public void notifyReachedDestination() {
					// Start with three waypoints in the pipe
					addToPath(new Vector3D((float)Math.random() * 500 + 100, (float)Math.random() * 500 + 100, 0.0f));
					addToPath(new Vector3D((float)Math.random() * 500 + 100, (float)Math.random() * 500 + 100, 0.0f));
					addToPath(new Vector3D((float)Math.random() * 500 + 100, (float)Math.random() * 500 + 100, 0.0f));
				}
				public void notifyNearingDestination() {
					// Always keep three waypoints in the pipe.  This allows us to compute cornering
					// angles, which will smooth out the motion.
					addToPath(new Vector3D((float)Math.random() * 500 + 100, (float)Math.random() * 500 + 100, 0.0f));			
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
		GUI.launchBattlefield("images/Background02.png");

		for (int i=0; i<5; i++)
			GUI.addPlayer(new RandomPathDriver(), "PatternMoverPhysics" + i);
	}
}
