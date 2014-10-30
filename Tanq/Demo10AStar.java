import uk.ac.derby.Tanq.GUI;
import uk.ac.derby.Tanq.Brains.*;
import uk.ac.derby.Tanq.Navigation3.Navigator;

// A* movement.
public class Demo10AStar {
	
	// A Brain that uses A* to move toward an opponent.
	static class NavigatorMover extends Brain {
		public void initialise() {
			Navigator navigator = new Navigator(this, 0.1f, true, 2) {
				// Set random destination -- either a random opponent if available, or a random location.
				public void notifyReachedDestination() {
					Opponent[] opponents = getOpponents();
					if (opponents.length == 0)
						setDestination(getRandomLocation());
					else
						setDestination(opponents[(int)(Math.random() * (double)opponents.length)].getLocation());
				}
			};
			navigator.setSpeedLimit(3.0f);
			navigator.start();
			setDebugging(true);
		}
	};
	
	/**
	 * An example of A*-based movement.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
//		GUI.launchBattlefield("images/Background06.png");
		GUI.launchBattlefield("images/Background02.png");
		
		GUI.addPlayer(new NavigatorMover(), "Navigator");
	}
}
