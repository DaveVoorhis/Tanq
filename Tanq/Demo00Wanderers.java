import sampleBrains.Blindman;
import sampleBrains.Drunkard;
import uk.ac.derby.Tanq.GUI;

public class Demo00Wanderers {
		
	/**
	 * An example of simple, random TanqS.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/Background03.png");
        
        for (int i=0; i<5; i++) {
        	GUI.addPlayer(new Blindman(), "Blindman" + i);
        	GUI.addPlayer(new Drunkard(), "Drunkard" + i);
        }
	}
}
