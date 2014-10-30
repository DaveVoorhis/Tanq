package uk.ac.derby.Tanq.Navigation2;
import java.util.*;
import java.util.Map.Entry;

import uk.ac.derby.GameEngine2D.*;

/*
 * An implementation of A*.
 */
public class AStar {

	private TreeMap<Float, Vector<AStarNode>> open;
	private boolean[][] isOpen;
	private boolean[][] isClosed;
	
	/** Set up Open and Closed lists, given the Map. */
	private final void configureLists(uk.ac.derby.GameEngine2D.Map map) {
		open = new TreeMap<Float, Vector<AStarNode>>();
		isOpen = new boolean[map.getWidth()][map.getHeight()];
		isClosed = new boolean[map.getWidth()][map.getHeight()];
	}
	
	/** Remove the Open node with the lowest cost from the Open list. */
	private final AStarNode obtainOpenWithLowestCost() {
		Iterator<Entry<Float, Vector<AStarNode>>> entries = open.entrySet().iterator();
		if (!entries.hasNext())
			return null;
		Vector<AStarNode> costEntry = entries.next().getValue();
		int lastCostEntry = costEntry.size() - 1;
		AStarNode lowestCostNode = costEntry.get(lastCostEntry);
		costEntry.remove(lastCostEntry);
		if (costEntry.size() == 0)
			entries.remove();
		isOpen[(int)lowestCostNode.getLocation().getX()][(int)lowestCostNode.getLocation().getY()] = false;
		return lowestCostNode;
	}
	
	/** Return true if there are items in the Open list. */
	private final boolean isOpenNodeAvailable() {
		return (open.size() > 0);
	}
	
	/** Add to Open list. */
	private final void addOpen(AStarNode node) {
		Float cost = new Float(node.getCost());
		Vector<AStarNode> costEntry = open.get(cost);
		// All nodes of the same cost are associated with the
		// same Open list entry.
		if (costEntry == null) {
			costEntry = new Vector<AStarNode>();
			open.put(cost, costEntry);
		}
		costEntry.add(node);
		isOpen[(int)node.getLocation().getX()][(int)node.getLocation().getY()] = true;
	}
	
	/** Add to Closed list. */
	private final void addClosed(AStarNode node) {
		isClosed[(int)node.getLocation().getX()][(int)node.getLocation().getY()] = true;
	}
	
	/** Return true if a given Location is in the Open list. */
	private final boolean isInOpen(AStarNode node) {
		return isOpen[(int)node.getLocation().getX()][(int)node.getLocation().getY()];
	}
	
	/** Return true if a given Location is in the Closed list. */
	private final boolean isInClosed(AStarNode node) {
		return isClosed[(int)node.getLocation().getX()][(int)node.getLocation().getY()];
	}
	
	/** Compute a path starting at the destination and working back to the start.
	 * This allows us to directly use the generated path, rather than having to reverse it, because the
	 * A* algorithm creates a path that points back to the A* algorithm's starting position.
	 * 
	 * @param destination - destination point
	 * @param start - start point
	 * @param uk.ac.derby.GameEngine2D.Map - map
	 * @return AStarNode - end node of path found, null if no path found 
	 */
	public AStarNode findPath(Vector3D start, Vector3D destination, uk.ac.derby.GameEngine2D.Map map) {
		// Configure map
		configureLists(map);
		// Push start to open.
		addOpen(new AStarNode(start));
		AStarNode current = null;
		while (isOpenNodeAvailable()) {
			current = obtainOpenWithLowestCost();
			if (Vector3D.getDistance(current.getLocation(), destination) <= 1) {
				return current;
			} else {
				addClosed(current);
				// Examine and cost out adjacent nodes, adding them to the open list if appropriate
				for (int x = (int)current.getLocation().getX() - 1; x <= current.getLocation().getX() + 1; x++) {
					if (x >= 0 && x < map.getWidth()) {
						for (int y = (int)current.getLocation().getY() - 1; y <= current.getLocation().getY() + 1; y++) {
							if (y >= 0 && y < map.getHeight()) {
								Vector3D adjacentPoint = new Vector3D(x, y, 0);
								AStarNode adjacentNode = new AStarNode(adjacentPoint, current);
								int mapValue = map.getMapValue(x, y);
								if (!isInOpen(adjacentNode) && !isInClosed(adjacentNode) && mapValue < 255) {
									adjacentNode.setG(Vector3D.getDistance(adjacentPoint, current.getLocation()) * 1.5f);
									adjacentNode.setH(Vector3D.getDistance(adjacentPoint, destination));
									adjacentNode.setT(mapValue * 1000);
									addOpen(adjacentNode);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	
}
