package uk.ac.derby.Tanq.Navigation3;

/** Abstract definition of an A* node. */
interface AStarNode {

	/** Node-to-node cost. */
	public abstract void setG(float newG);

	/** Node-to-destination cost. */
	public abstract void setH(float newH);

	/** Barrier cost. */
	public abstract void setT(float newT);

	public abstract float getCost();

	public abstract AStarNode getParent();

	public abstract int compareTo(AStarNode o);

	public abstract boolean equals(Object o);
	
	public abstract boolean isTouching(AStarNode o);

}