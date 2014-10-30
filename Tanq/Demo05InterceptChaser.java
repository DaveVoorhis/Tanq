import sampleBrains.InterceptionChaser;
import sampleBrains.Madman;
import uk.ac.derby.Tanq.GUI;
import uk.ac.derby.Tanq.Brains.*;

public class Demo05InterceptChaser {
	
	public static InterceptionChaser chaser = new InterceptionChaser();
	public static Brain evader = new Madman();
	
	/**
	 * An example of an intercept chaser vs. a random evader.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/Background05.png");

		chaser.setOpponent(evader);
		
		GUI.addPlayer(chaser, "InterceptChaser");
		GUI.addPlayer(evader, "RandomEvader");
	}
}
