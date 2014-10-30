import sampleBrains.Blindman;
import sampleBrains.Drunkard;
import sampleBrains.FiniteStateMachine;
import sampleBrains.Madman;
import sampleBrains.Weirdo;
import uk.ac.derby.Tanq.GUI;

/** Competition environment.
 * 
 * @author Dave
 *
 */
public class Competition {

	private static final int competitionDurationMinutes = 10;
	
	/**
	 * Tanq competition.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GUI.main(args);
		
		GUI.launchBattlefield("images/BackgroundCompetition08.png");
		GUI.setCompetitionTime(competitionDurationMinutes, "Final.txt");
		
		GUI.addPlayer(new Blindman(), "Blindman");
		GUI.addPlayer(new Drunkard(), "Drunkard");
		GUI.addPlayer(new FiniteStateMachine(), "FSM");
		GUI.addPlayer(new Madman(), "Madman");
		GUI.addPlayer(new Weirdo(true), "Weirdo");
	}
}
