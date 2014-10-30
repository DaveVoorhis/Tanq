package uk.ac.derby.GameEngine2D;

import uk.ac.derby.Logger.*;

/** A map provides a map of terrain. 
 * 
 * IMPORTANT NOTE:  See getMapValue() for information on how to interpret the map.
 */
public class Map {

	private float scaleFactor;	// inverse scale, precalculated for speed
	private Arena theArena;
	private int[][] map;
	private int mapwidth;
	private int mapheight;

	/** Create a map that is a scaled model of the Arena's map image.
	 * 
	 * @param theScale - 1.0 = 1:1 map.  0.5 = half-scale map
	 * @param arena
	 * @param blur - > 0 to create an semi-passable buffer zone around terrain pixels
	 */
	public Map(float theScale, Arena arena, int blur) {
		scaleFactor = 1.0f / theScale;
		theArena = arena;
		mapwidth = (int)(theArena.getLandscapeWidth() / scaleFactor);
		mapheight = (int)(theArena.getLandscapeHeight() / scaleFactor);
		map = new int[mapwidth][mapheight];
		if (blur > 0) {
			for (int x=0; x<getWidth(); x++)
				for (int y=0; y<getHeight(); y++) {
					int mapValue = (theArena.isBlockedByTerrain((int)(x * scaleFactor), (int)(y * scaleFactor), 
							(int)scaleFactor, (int)scaleFactor)) ? 255 : 0;
					if (mapValue >= 255) {
						for (int i = x - blur; i <= x + blur; i++)
							if (i>=0 && i<getWidth())
								for (int j = y - blur; j <= y + blur; j++)
									if (j>=0 && j<getHeight() && map[i][j]==0)
										map[i][j] = 127;
						map[x][y] = mapValue;
					}
				}
		} else {
			for (int x=0; x<getWidth(); x++)
				for (int y=0; y<getHeight(); y++)
					map[x][y] = (theArena.isBlockedByTerrain((int)(x * scaleFactor), (int)(y * scaleFactor), 
							(int)scaleFactor, (int)scaleFactor)) ? 255 : 0;			
		}
	}
	
	/** Return map value given a real-world coordinate.  The higher the map value,
	 * the less passable the terrain.  In other words, 0 means 100% passable. */
	public final int getWorldMapValue(int worldX, int worldY) {
		return map[(int)(worldX / scaleFactor)][(int)(worldY / scaleFactor)];
	}
	
	/** Return map value given a map coordinate.  The higher the map value,
	 * the less passable the terrain.  
	 * 
	 * 0 means 100% passable.
	 * 
	 * 0 < getMapValue() < 255 is passable to a certain degree.
	 * 
	 * Currently, any value >= 255 is unpassable.
	 * 
	 */
	public int getMapValue(int x, int y) {
		return map[x][y];
	}
	
	/** Return width of map. */
	public int getWidth() {
		return mapwidth;
	}
	
	/** Return height of map. */
	public int getHeight() {
		return mapheight;
	}
	
	/** Dump map to Log. */
	public void dump() {
		Log.println("Map width = " + getWidth());
		Log.println("Map height = " + getHeight());
		for (int y=0; y<getHeight(); y++) {
			for (int x=0; x<getWidth(); x++) {
				int mapValue = getMapValue(x, y);
				Log.print(mapValue==0 ? " " : mapValue >= 255 ? "o" : ".");				
			}
			Log.println();
		}
	}
}
