import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.GUI;
import uk.ac.derby.Tanq.Brains.*;

public class Demo06PatternMovementBasic {

	private final static double velocity = 1.0;
	private final static float cornerTolerance = 5.0f;
		
	public static Brain mover = new Brain() {
		Vector3D movements[] = {
				new Vector3D(100.0f, 100.0f, 0.0f),
				new Vector3D(500.0f, 100.0f, 0.0f),
				new Vector3D(500.0f, 300.0f, 0.0f),
				new Vector3D(100.0f, 300.0f, 0.0f)
		};
		private int target = 0;
		public void compute() {
			Vector3D location = getLocation();
			float x = location.getX();
			float y = location.getY();
			// Move toward current target
			if (x < movements[target].getX())
				x += velocity;
			else if (x > movements[target].getX())
				x -= velocity;
			if (y < movements[target].getY())
				y += velocity;
			else if (y > movements[target].getY())
				y -= velocity;
			// Are we close enough to the target to move on?
			if (Math.abs(movements[target].getX() - x) < cornerTolerance && 
				Math.abs(movements[target].getY() - y) < cornerTolerance) {
				target++;
				if (target >= movements.length)
					target = 0;
			}
			// Set new location
			setLocation(new Vector3D(x, y, 0.0f));
			// Delay lets other things happen
			Utility.delay(5);
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

		GUI.addPlayer(mover, "PatternMoverBasic");
	}
}
