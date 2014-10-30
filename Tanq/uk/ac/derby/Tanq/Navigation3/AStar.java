package uk.ac.derby.Tanq.Navigation3;

/*
 * An implementation of A*.
 */
public class AStar {

	private AStarEngine aStar;
	
	public AStar(AStarEngine core) {
		this.aStar = core;
	}
	
	/** Compute a path starting at the destination and working back to the start.
	 * This allows us to directly use the generated path, rather than having to reverse it, because the
	 * A* algorithm creates a path that points back to the A* algorithm's starting position.
	 * 
	 * @param destination - destination point
	 * @param start - start point
	 * @return AStarNode - end node of path found, null if no path found 
	 */
	public AStarNode findPath(AStarNode start, AStarNode destination) {
		// Push start to open.
		aStar.addOpen(start);
		while (aStar.isOpenNodeAvailable()) {
			AStarNode current = aStar.obtainOpenNodeWithLowestCost();
			if (current.isTouching(destination)) {
				return current;
			} else {
				aStar.addClosed(current);
				// Examine and cost out adjacent nodes, adding them to the open list if appropriate
				for (AStarNode adjacentNode: aStar.getAdjacentTo(current)) {
					if (!aStar.isInOpen(adjacentNode) && !aStar.isInClosed(adjacentNode) && aStar.canBeInPath(adjacentNode)) {
						aStar.setFactors(current, destination, adjacentNode);
						aStar.addOpen(adjacentNode);
					}
				}
			}
		}
		return null;
	}
	
}
