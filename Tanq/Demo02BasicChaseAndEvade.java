import sampleBrains.BasicChaser;
import sampleBrains.BasicEvader;
import uk.ac.derby.Tanq.GUI;

public class Demo02BasicChaseAndEvade {
	
	public static BasicEvader evader = new BasicEvader();
	public static BasicChaser chaser = new BasicChaser();
	
	/**
	 * 
	 * An example of basic chasing and evading.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/Background05.png");
		
		evader.setOpponent(chaser);
		chaser.setOpponent(evader);
		
		GUI.addPlayer(chaser, "BasicChaser");
		GUI.addPlayer(evader, "BasicEvader");
	}
}
