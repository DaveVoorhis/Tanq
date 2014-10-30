package uk.ac.derby.Tanq.Navigation3;

import uk.ac.derby.GameEngine2D.Vector3D;

/**
 * A node for use by an A* algorithm that uses Vector3D to represent a location on a 2D map.
 */

class AStarNodeVector3D implements Comparable<AStarNode>, AStarNode {
	
	private AStarNode parent = null;
	private float g = 0; 	// g factor (actual distance of path)
	private float h = 0;  // h factor (estimated distance to destination)
	private float t = 0;  // t factor (terrain cost)
	private Vector3D location;	// location of this node

	public AStarNodeVector3D(Vector3D position) {
		location = position;
	}
	
	public AStarNodeVector3D(Vector3D position, AStarNode parentNode) {
		location = position;
		parent = parentNode;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.derby.Tanq.Navigation3._AStarNode#setG(float)
	 */
	public final void setG(float newG) {
		g = newG;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.derby.Tanq.Navigation3._AStarNode#setH(float)
	 */
	public final void setH(float newH) {
		h = newH;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.derby.Tanq.Navigation3._AStarNode#setT(float)
	 */
	public final void setT(float newT) {
		t = newT;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.derby.Tanq.Navigation3._AStarNode#getCost()
	 */
	public final float getCost() {
		return g + h + t;
	}
	
	public final Vector3D getLocation() {
		return location;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.derby.Tanq.Navigation3._AStarNode#getParent()
	 */
	public final AStarNode getParent() {
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.derby.Tanq.Navigation3._AStarNode#compareTo(uk.ac.derby.Tanq.Navigation3.AStarNode)
	 */
	public int compareTo(AStarNode o) {
		return (int)Vector3D.getDistance(location, ((AStarNodeVector3D)o).location);
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.derby.Tanq.Navigation3._AStarNode#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return (compareTo((AStarNode)o) == 0);
	}
	
	public String toString() {
		return "<" + location + ", " + getCost() + ">";
	}

	public boolean isTouching(AStarNode o) {
		return Vector3D.getDistance(getLocation(), ((AStarNodeVector3D)o).getLocation()) <= 1;
	}
	
}
