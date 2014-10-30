import sampleBrains.LineOfSightChaser;
import sampleBrains.Madman;
import uk.ac.derby.Tanq.GUI;
import uk.ac.derby.Tanq.Brains.*;

public class Demo04LineOfSightChase {
	
	public static LineOfSightChaser chaser = new LineOfSightChaser();
	public static Brain evader = new Madman();
	
	/**
	 * An example of line of sight chaser vs. a random evader.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/Background05.png");

		chaser.setOpponent(evader);
		
		GUI.addPlayer(chaser, "LineOfSightChaser");
		GUI.addPlayer(evader, "RandomEvader");
	}
}
