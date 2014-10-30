package uk.ac.derby.Tanq.Navigation3;

import java.util.LinkedList;

/** Abstract definition of the core functionality required to support the A* algorithm implemented in AStar.java */
public interface AStarEngine {
	
	/** Remove the Open node with the lowest cost from the Open list. */
	public AStarNode obtainOpenNodeWithLowestCost();
	
	/** Return true if there are items in the Open list. */
	public boolean isOpenNodeAvailable();
	
	/** Add to Open list. */
	public void addOpen(AStarNode sourceNode);
	
	/** Remove from Open list. */
	public void removeOpen(AStarNode sourceNode);
	
	/** Add to Closed list. */
	public void addClosed(AStarNode sourceNode);
	
	/** Return true if a given Location is in the Open list. */
	public boolean isInOpen(AStarNode sourceNode);
	
	/** Return true if a given Location is in the Closed list. */
	public boolean isInClosed(AStarNode sourceNode);
	
	/** Return all nodes adjacent to a given node. */
	public LinkedList<AStarNode> getAdjacentTo(AStarNode sourceNode);

	/** Return true if given node can be part of the traversable path. */
	public boolean canBeInPath(AStarNode sourceNode);

	/** Set factors on given node. */
	public void setFactors(AStarNode current, AStarNode destination, AStarNode newNode);
	
}
