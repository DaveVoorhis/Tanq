package uk.ac.derby.Tanq.Core;

public class Version {

	public static String getProductName() {
		return "Tanq";
	}

	public static int getMajor() {
		return 0;
	}
	
	public static int getMinor() {
		return 0;
	}
	
	public static int getRevision() {
		return 13;
	}
	
	public static String getRelease() {
		return "Dev";
	}
	
	public static String getVersion() {
		return getProductName() + " " + getMajor() + "." + getMinor() + "." + getRevision() + " " + getRelease();
	}
}
