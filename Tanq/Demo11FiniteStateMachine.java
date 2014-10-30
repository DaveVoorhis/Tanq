import sampleBrains.FiniteStateMachine;
import uk.ac.derby.Tanq.GUI;

/** A Tanq Brain based on a finite state machine..
 * 
 * @author Dave
 *
 * Based on Task #7:
 *
 * Try implementing a Brain based on a finite state machine that 
 * appropriately transitions between the following states:
 * 
 *   Idling - if no enemy Tanqs are within a given distance, 
 *            randomly explore the game environment;
 *   Seeking - if an enemy Tanq comes within a given distance, 
 *             use pathfinding to chase it;
 *   Attacking - if an enemy Tanq is close, engage it in battle, 
 *               using gun to attack it;
 *   Retreat - if gun has overheated, temporarily retreat from enemy;
 *   Defending - if an enemy Tanq has hit you, run!  Go back to 
 *               idling when you�re far enough away, and have 
 *               recovered from the scare.
 */
public class Demo11FiniteStateMachine {
	
	/**
	 * An example of FSM-based behaviour.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/Background06.png");
//		GUI.launchBattlefield("images/Background01.png");
		
		GUI.addPlayer(new FiniteStateMachine(), "FSM01");
		GUI.addPlayer(new FiniteStateMachine(), "FSM01");
		GUI.addPlayer(new FiniteStateMachine(), "FSM01");
		GUI.addPlayer(new FiniteStateMachine(), "FSM01");
		GUI.addPlayer(new FiniteStateMachine(), "FSM01");
	}
}
