import sampleBrains.BasicChaser;
import sampleBrains.Madman;
import uk.ac.derby.Tanq.GUI;

public class Demo03BasicChaseRandomEvade {
	
	public static BasicChaser chaser = new BasicChaser();
	public static Madman evader = new Madman();
	
	/**
	 * An example of basic chaser vs. a random evader.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/Background05.png");

		chaser.setOpponent(evader);
		
		GUI.addPlayer(chaser, "BasicChaser");
		GUI.addPlayer(evader, "RandomEvader");
	}
}
