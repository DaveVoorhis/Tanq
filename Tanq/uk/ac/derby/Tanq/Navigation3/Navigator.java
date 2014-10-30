package uk.ac.derby.Tanq.Navigation3;
import java.util.*;

import uk.ac.derby.GameEngine2D.*;
import uk.ac.derby.Tanq.Brains.*;

/*
 * A Navigator is a Driver that uses the A* algorithm to find its way.
 */
public class Navigator extends Driver {
	
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
		uk.ac.derby.GameEngine2D.Map map = getBrain().getBlurredMap(mapScale, mapBlur);
		// Get start of A* (which is where we want to go) location as a map coordinate
		start = dest.multiply(mapScale);
		// Get the A* destination (our current location) as a map coordinate
		destination = getBrain().getLocation().multiply(mapScale);
		
		// Here's where we use the new A* implementation
		AStar astar = new AStar(new AStarEngineXY(map));
		AStarNode endOfPath = astar.findPath(new AStarNodeVector3D(start), new AStarNodeVector3D(destination));

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
		if (endOfPath != null) {
			// Set new path.
			while (endOfPath != null) {
				Vector3D waypoint = Vector3D.divide(((AStarNodeVector3D)endOfPath).getLocation(), mapScale);
				if (getBrain().isDebugging()) {
					pathMarkers.add(getBrain().markPoint(waypoint));
					getBrain().println("Waypoint " + waypoint);
				}
				addToPath(waypoint);
				endOfPath = endOfPath.getParent();
			}
			// Go!
			setSpeedLimit(3.0f);
			start();
			return true;
		} else
			return false;
	}
	
}
