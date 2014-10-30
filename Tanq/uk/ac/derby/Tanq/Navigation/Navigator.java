package uk.ac.derby.Tanq.Navigation;
import java.util.*;
import java.util.Map.Entry;

import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.Brains.*;

/*
 * A Navigator is a Driver that uses the A* algorithm to find its way.
 */
public class Navigator extends Driver {

	private TreeMap<Float, Vector<AStarNode>> open;
	private boolean[][] isOpen;
	private boolean[][] isClosed;
	
	private Vector3D start;
	private Vector3D destination;
	private float mapScale = 0.1f;
	private int mapBlur = 1;
	private Vector<SpriteStatic> pathMarkers;
	
	/*
	 * Create a Navigator, which will use A* to compute a path.
	 * 
	 * @param brain - the Brain that this Navigator will control.
	 * @param scale - the map scale. 1 == 1:1, 0.25 = 4:1, etc.
	 * @param cornerSlowDown - if true, the sharper the turn the slower the turn
	 * @param blur - size of avoidable buffer zone around terrain.  The higher the value, the more
	 * we're forced away from terrain.  0 means we can bump terrain. 
	 * 
	 */
	public Navigator(Brain brain, float scale, boolean cornerModerate, int blur) {
		super(brain);
		mapScale = scale;
		mapBlur = blur;
		setCorneringAdjustmentEnabled(cornerModerate);
	}
	
	/** Set up Open and Closed lists, given the Map. */
	private void configureLists(uk.ac.derby.GameEngine2D.Map map) {
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
	public final void addOpen(AStarNode node) {
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
	public final void addClosed(AStarNode node) {
		isClosed[(int)node.getLocation().getX()][(int)node.getLocation().getY()] = true;
	}
	
	/** Return true if a given Location is in the Open list. */
	public final boolean isInOpen(AStarNode node) {
		return isOpen[(int)node.getLocation().getX()][(int)node.getLocation().getY()];
	}
	
	/** Return true if a given Location is in the Closed list. */
	public final boolean isInClosed(AStarNode node) {
		return isClosed[(int)node.getLocation().getX()][(int)node.getLocation().getY()];
	}
	
	/** Set the destination.  This will compute a path and follow it.  Return true if the destination
	 * can be reached, false if not.  If we can't reach the destination, we won't move.
	 * 
	 * Internally, A* is used to compute a path starting at the destination and working back to the start.
	 * This allows us to directly use the generated path, rather than having to reverse it, because the
	 * A* algorithm creates a path that points back to the A* algorithm's starting position.
	 * 
	 * @param dest - destination point
	 */
	public boolean setDestination(Vector3D dest) {
		if (getBrain().isDebugging())
			getBrain().println("Navigator asked to plot path from " + getBrain().getLocation() + " to " + dest);
		boolean destinationFound = false;
		uk.ac.derby.GameEngine2D.Map map = getBrain().getBlurredMap(mapScale, mapBlur);
		// Configure map
		configureLists(map);
		// Get start of A* (which is where we want to go) location as a map coordinate
		start = dest.multiply(mapScale);
		// Get the A* destination (our current location) as a map coordinate
		destination = getBrain().getLocation().multiply(mapScale);
		// Push start to open.
		addOpen(new AStarNode(start));
		AStarNode current = null;
		while (isOpenNodeAvailable()) {
			current = obtainOpenWithLowestCost();
			if (Vector3D.getDistance(current.getLocation(), destination) <= 1) {
				if (getBrain().isDebugging())
					getBrain().println("Destination reached.");
				destinationFound = true;
				break;
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
		// Clear any existing path.
		clearPath();
		// If we're debugging, clear previous path markers.
		if (getBrain().isDebugging()) {
			if (pathMarkers != null) {
				for (SpriteStatic i: pathMarkers)
					i.suicide();
			} 
			pathMarkers = new Vector<SpriteStatic>();
		}
		// Found a destination?  If so, go there.
		if (destinationFound) {
			// Set new path.
			while (current != null) {
				Vector3D waypoint = Vector3D.divide(current.getLocation(), mapScale);
				if (getBrain().isDebugging()) {
					pathMarkers.add(getBrain().markPoint(waypoint));
					getBrain().println("Waypoint " + waypoint);
				}
				addToPath(waypoint);
				current = current.getParent();
			}
			// Go!
			setSpeedLimit(3.0f);
			start();
		}
		return destinationFound;
	}
	
}
