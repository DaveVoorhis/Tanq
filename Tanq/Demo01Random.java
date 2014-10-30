import sampleBrains.Madman;
import uk.ac.derby.Tanq.GUI;

public class Demo01Random {
	/**
	 * An example of simple, random TanqS.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		GUI.launchBattlefield("images/Background03.png");
        
        for (int i=0; i<10; i++) {
        	GUI.addPlayer(new Madman(), "Tanq" + i);
        }
	}
}
