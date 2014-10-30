package uk.ac.derby.Tanq.Navigation2;
import uk.ac.derby.GameEngine2D.Vector3D;

/**
 * A node for use by an A* algorithm that uses Vector3D to represent a game state.
 */

class AStarNode implements Comparable<AStarNode> {
	
	private AStarNode parent = null;
	private float g = 0; 	// g factor (actual distance of path)
	private float h = 0;  // h factor (estimated distance to destination)
	private float t = 0;  // t factor (terrain cost)
	private Vector3D location;	// location of this node

	public AStarNode(Vector3D position) {
		location = position;
	}
	
	public AStarNode(Vector3D position, AStarNode parentNode) {
		location = position;
		parent = parentNode;
	}
	
	public final void setG(float newG) {
		g = newG;
	}
	
	public final float getG() {
		return g;
	}
	
	public final void setH(float newH) {
		h = newH;
	}
	
	public final float getH() {
		return h;
	}
	
	public final void setT(float newT) {
		t = newT;
	}
	
	public final float getT() {
		return t;
	}
	
	public final float getCost() {
		return g + h + t;
	}
	
	public final Vector3D getLocation() {
		return location;
	}
	
	public final AStarNode getParent() {
		return parent;
	}
	
	public int compareTo(AStarNode o) {
		return (int)Vector3D.getDistance(location, o.location);
	}
	
	public boolean equals(Object o) {
		return (compareTo((AStarNode)o) == 0);
	}
	
	public String toString() {
		return "<" + location + ", " + getCost() + ">";
	}
}