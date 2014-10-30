package uk.ac.derby.Tanq.Core;

import javax.swing.*;

public class TanqRanked implements Comparable<TanqRanked> {

	private Tanq tanq;
	
	TanqRanked(Tanq t) {
		tanq = t;
	}

	public Tanq getTanq() {
		return tanq;
	}
	
	public String toString() {
		return tanq.getSpriteName() + ": " + tanq.getScore() + ", " + tanq.getInjuries() + ", " + tanq.getCollisions() + ", " + tanq.getBumps();
	}

	public String getName() {
		return tanq.getSpriteName();
	}
	
	public int getScore() {
		return tanq.getScore();
	}
	
	public int getInjuries() {
		return tanq.getInjuries();
	}
	
	public int getCollisions() {
		return tanq.getCollisions();
	}
	
	public int getBumps() {
		return tanq.getBumps();
	}
	
	public int getBulletsAvailable() {
		return tanq.getBulletsAvailable();
	}
	
	public ImageIcon getIcon() {
		return tanq.getImage();
	}
	
	public int compareTo(TanqRanked t) {
		int scoreDiff = (t.tanq.getScore() - tanq.getScore());
		if (scoreDiff != 0)
			return scoreDiff;
		int injuriesDiff = - (t.tanq.getInjuries() - tanq.getInjuries());
		if (injuriesDiff != 0)
			return injuriesDiff;
		int collisionsDiff = - (t.tanq.getCollisions() - tanq.getCollisions());
		if (collisionsDiff != 0)
			return collisionsDiff;
		int bumpsDiff = - (t.tanq.getBumps() - tanq.getBumps());
		return bumpsDiff;
	}
}
