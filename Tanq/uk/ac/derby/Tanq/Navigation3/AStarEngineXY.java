package uk.ac.derby.Tanq.Navigation3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import uk.ac.derby.GameEngine2D.Vector3D;

/**
 * Core functionality for an A* algorithm that finds paths between locations on a 2D map.
 */
public class AStarEngineXY implements AStarEngine {
	
	private TreeMap<Float, Vector<AStarNodeVector3D>> open = new TreeMap<Float, Vector<AStarNodeVector3D>>();
	private boolean[][] isOpen;
	private boolean[][] isClosed;
	private uk.ac.derby.GameEngine2D.Map map;
	
	/** Set up Open and Closed lists, given the Map. */
	public AStarEngineXY(uk.ac.derby.GameEngine2D.Map map) {
		open = new TreeMap<Float, Vector<AStarNodeVector3D>>();
		isOpen = new boolean[map.getWidth()][map.getHeight()];
		isClosed = new boolean[map.getWidth()][map.getHeight()];
		this.map = map;
	}
	
	/** Remove the Open node with the lowest cost from the Open list. */
	public AStarNode obtainOpenNodeWithLowestCost() {
		Iterator<Entry<Float, Vector<AStarNodeVector3D>>> entries = open.entrySet().iterator();
		if (!entries.hasNext())
			return null;
		Vector<AStarNodeVector3D> costEntry = entries.next().getValue();
		int lastCostEntry = costEntry.size() - 1;
		AStarNode lowestCostNode = costEntry.get(lastCostEntry);
		costEntry.remove(lastCostEntry);
		if (costEntry.size() == 0)
			entries.remove();
		removeOpen(lowestCostNode);
		return lowestCostNode;
	}
	
	/** Return true if there are items in the Open list. */
	public boolean isOpenNodeAvailable() {
		return (open.size() > 0);
	}
	
	/** Add to Open list. */
	public void addOpen(AStarNode sourceNode) {
		AStarNodeVector3D node = (AStarNodeVector3D)sourceNode;
		Float cost = new Float(node.getCost());
		Vector<AStarNodeVector3D> costEntry = open.get(cost);
		// All nodes of the same cost are associated with the
		// same Open list entry.
		if (costEntry == null) {
			costEntry = new Vector<AStarNodeVector3D>();
			open.put(cost, costEntry);
		}
		costEntry.add(node);
		isOpen[(int)node.getLocation().getX()][(int)node.getLocation().getY()] = true;
	}
	
	/** Remove from Open list. */
	public void removeOpen(AStarNode sourceNode) {
		AStarNodeVector3D node = (AStarNodeVector3D)sourceNode;
		isOpen[(int)node.getLocation().getX()][(int)node.getLocation().getY()] = false;		
	}
	
	/** Add to Closed list. */
	public void addClosed(AStarNode sourceNode) {
		AStarNodeVector3D node = (AStarNodeVector3D)sourceNode;
		isClosed[(int)node.getLocation().getX()][(int)node.getLocation().getY()] = true;
	}
	
	/** Return true if a given Location is in the Open list. */
	public boolean isInOpen(AStarNode sourceNode) {
		AStarNodeVector3D node = (AStarNodeVector3D)sourceNode;
		return isOpen[(int)node.getLocation().getX()][(int)node.getLocation().getY()];
	}
	
	/** Return true if a given Location is in the Closed list. */
	public boolean isInClosed(AStarNode sourceNode) {
		AStarNodeVector3D node = (AStarNodeVector3D)sourceNode;
		return isClosed[(int)node.getLocation().getX()][(int)node.getLocation().getY()];
	}

	public LinkedList<AStarNode> getAdjacentTo(AStarNode sourceNode) {
		LinkedList<AStarNode> adjacent = new LinkedList<AStarNode>();
		AStarNodeVector3D current = (AStarNodeVector3D)sourceNode;
		for (int x = (int)current.getLocation().getX() - 1; x <= current.getLocation().getX() + 1; x++) {
			if (x >= 0 && x < map.getWidth()) {
				for (int y = (int)current.getLocation().getY() - 1; y <= current.getLocation().getY() + 1; y++) {
					if (y >= 0 && y < map.getHeight())
						if (!(x == (int)current.getLocation().getX() && y == (int)current.getLocation().getY()))
							adjacent.add(new AStarNodeVector3D(new Vector3D(x, y, 0), current));
				}
			}
		}
		return adjacent;
	}

	private int getMapValue(AStarNodeVector3D node) {
		return map.getMapValue((int)node.getLocation().getX(), (int)node.getLocation().getY());
	}
	
	public boolean canBeInPath(AStarNode sourceNode) {
		return getMapValue((AStarNodeVector3D)sourceNode) < 255;
	}

	public void setFactors(AStarNode current, AStarNode destination, AStarNode newNode) {
		AStarNodeVector3D node = (AStarNodeVector3D)newNode;
		node.setG(Vector3D.getDistance(node.getLocation(), ((AStarNodeVector3D)current).getLocation()));
		node.setH(Vector3D.getDistance(node.getLocation(), ((AStarNodeVector3D)destination).getLocation()));
		node.setT(getMapValue((AStarNodeVector3D)newNode));
	}

}
