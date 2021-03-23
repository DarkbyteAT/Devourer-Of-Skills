package net.atlne.dos.game.maps;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import net.atlne.dos.Core;
import net.atlne.dos.game.maps.objects.CollisionTile;
import net.atlne.dos.game.maps.objects.LightSource;
import net.atlne.dos.graphics.GraphicsManager;

public class Map {
	
	/**Stores the tile map to render.*/
	protected TiledMap tileMap;
	
	/**Stores the width of the map.*/
	protected int width;
	/**Stores the height of the map.*/
	protected int height;
	/**Stores whether the room is inside or outside.*/
	protected boolean outside;
	/**Stores the minimum ambient light level of the room.*/
	protected float minLight;
	/**Stores the maximum ambient light level of the room.*/
	protected float maxLight;
	
	/**Stores the layers to render in the background.*/
	protected int[] background;
	/**Stores the layers to render in the foreground.*/
	protected int[] foreground;
	/**Stores the collision terrain tile objects.*/
	protected Set<CollisionTile> impasseTiles = ConcurrentHashMap.newKeySet();
	/**Stores the collision tile objects.*/
	protected Set<CollisionTile> collisionTiles = ConcurrentHashMap.newKeySet();
	/**Stores the light sources for the map.*/
	protected Vector<LightSource> lightSources = new Vector<>();
	/**Stores the reference objects within the map, mapped to their names.*/
	protected ConcurrentHashMap<String, MapObject> referenceObjects = new ConcurrentHashMap<>();

	public Map(TiledMap tileMap) {
		this.tileMap = tileMap;
		
		/**Stores vectors of all background and foreground layers.*/
		Vector<Integer> background = new Vector<Integer>(),
				foreground = new Vector<Integer>();
		/**Stores all of the bridged tiles to ignore impasse tiles.*/
		HashSet<Vector2> bridgePositions = new HashSet<>();
		
		/**Obtains map properties from the file.*/
		this.width = tileMap.getProperties().get("width", int.class);
		this.height = tileMap.getProperties().get("height", int.class);
		this.outside = tileMap.getProperties().containsKey("outside") ?
				tileMap.getProperties().get("outside", boolean.class) : false;
		this.minLight = tileMap.getProperties().containsKey("min-light") ? 
				tileMap.getProperties().get("min-light", float.class) :
					outside ? 0.2f : 0.1f;
		this.maxLight = tileMap.getProperties().containsKey("max-light") ? 
				tileMap.getProperties().get("max-light", float.class) :
					outside ? 1f : 0.9f;
		
		/**Iterates through each layer of the map to determine layer data.*/
		for(int i = 0; i < tileMap.getLayers().getCount(); i++) {
			/**Gets the layer.*/
			MapLayer layer = tileMap.getLayers().get(i);
			
			/**Determines the draw order of the layer*/
			if(layer.getProperties().containsKey("background") && layer.getProperties().get("background", boolean.class))
				background.add(i);
			if(layer.getProperties().containsKey("foreground") && layer.getProperties().get("foreground", boolean.class))
				foreground.add(i);
			
			/**Handles layer types (reference, spawner, light-map, impasse, or collision).*/
			if(layer.getProperties().containsKey("reference") && layer.getProperties().get("reference", boolean.class)) {
				createReferences(layer);
			} else if(layer.getProperties().containsKey("spawner") && layer.getProperties().get("spawner", boolean.class)) {
				createSpawners(layer);
			} else if(layer.getProperties().containsKey("light-map") && layer.getProperties().get("light-map", boolean.class)) {
				createLightSources(layer);
			} else if(layer.getProperties().containsKey("impasse") && layer.getProperties().get("impasse", boolean.class)) {
				createTiles((TiledMapTileLayer) layer, true);
			} else if(layer.getProperties().containsKey("collision") && layer.getProperties().get("collision", boolean.class)) {
				createTiles((TiledMapTileLayer) layer, false);
			} else if(layer.getProperties().containsKey("bridge") && layer.getProperties().get("bridge", boolean.class)) {
				TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
				
				for(int x = 0; x < width; x++)
					for(int y = 0; y < height; y++)
						if(tileLayer.getCell(x, y) != null)
							bridgePositions.add(new Vector2(x + 0.5f, y + 0.5f));
			}
		}
		
		/**Removes all impasse tiles on bridge positions.*/
		for(CollisionTile impasseTile : impasseTiles) {
			Vector2 impasseTilePosition = impasseTile.getPosition().cpy();
			impasseTilePosition.x = (int) impasseTilePosition.x + 0.5f;
			impasseTilePosition.y = (int) impasseTilePosition.y + 0.5f;
			
			if(bridgePositions.contains(impasseTilePosition))
				impasseTiles.remove(impasseTile);
		}
		
		/**Iterates over the edges of the map and adds borders.*/
		for(int x = 0; x <= width; x++) {
			(outside ? impasseTiles : collisionTiles).add(new CollisionTile(new Vector2(x + 0.5f, -0.5f), 0, Core.getMaps().getTileShape(), outside));
			(outside ? impasseTiles : collisionTiles).add(new CollisionTile(new Vector2(x + 0.5f, height + 0.5f), 0, Core.getMaps().getTileShape(), outside));
		}
		
		for(int y = 0; y <= height; y++) {
			(outside ? impasseTiles : collisionTiles).add(new CollisionTile(new Vector2(-0.5f, y + 0.5f), 0, Core.getMaps().getTileShape(), outside));
			(outside ? impasseTiles : collisionTiles).add(new CollisionTile(new Vector2(width + 0.5f, y + 0.5f), 0, Core.getMaps().getTileShape(), outside));
		}
		
		/**Initialises the background and foreground arrays.*/
		this.background = new int[background.size()];
		this.foreground = new int[foreground.size()];
		for(int i = 0; i < background.size(); i++)
			this.background[i] = background.get(i);
		for(int i = 0; i < foreground.size(); i++)
			this.foreground[i] = foreground.get(i);
	}
	
	/**Disposes of any assets for the map.*/
	public void dispose() {
		tileMap.dispose();
	}
	
	/**Creates reference objects for the map.*/
	public void createReferences(MapLayer layer) {
		for(MapObject obj : layer.getObjects())
			referenceObjects.put(obj.getProperties().containsKey("name") ?
					obj.getProperties().get("name", String.class) : Integer.toString(referenceObjects.size()), obj);
	}
	
	/**Creates spawners for the map.*/
	public void createSpawners(MapLayer layer) {
		//TODO
	}
	
	/**Creates light sources from the given light-map layer.*/
	public void createLightSources(MapLayer layer) {
		for(MapObject lightObj : layer.getObjects())
			if(lightObj instanceof RectangleMapObject)
				lightSources.add(new LightSource((RectangleMapObject) lightObj));
	}
	
	/**Creates collision tiles from the given impasse layer.*/
	public void createTiles(TiledMapTileLayer layer, boolean impasse) {
		for(int x = 0; x < width; x++)
			for(int y = 0; y < height; y++) {
				if(layer.getCell(x, y) != null) {
					boolean objects = false;
					int rotation = layer.getCell(x, y).getRotation();
					if(rotation != 0)
						System.out.println(rotation + ", " + x + ", " + y);
					MapObjects tileCollisionObjs = layer.getCell(x, y).getTile().getObjects();
					
					/**Iterates over any existing collision objects.*/
					for(MapObject obj : tileCollisionObjs) {
						objects = true;
						
						/**Handles both rectangles and polygons.*/
						if(obj instanceof RectangleMapObject) {
							Rectangle rect = ((RectangleMapObject) obj).getRectangle();
							Vector2 size = rect.getSize(new Vector2()),
									position  = rect.getPosition(new Vector2()),
									center = new Vector2(position.x + (size.x / 2), position.y + (size.y / 2))
														.scl(1 / GraphicsManager.PPM).add(x, y);
							CollisionTile tile = new CollisionTile(center, rotation, Core.getPhysicsShapes().loadRectangle(size.scl(0.5f / GraphicsManager.PPM)), impasse);
							(impasse ? impasseTiles : collisionTiles).add(tile);
						} else if(obj instanceof PolygonMapObject) {
							Polygon polygon = ((PolygonMapObject) obj).getPolygon();
							Vector2 center = new Vector2(polygon.getX(), polygon.getY()).scl(1 / GraphicsManager.PPM).add(x, y);
							CollisionTile tile = new CollisionTile(center, rotation, Core.getPhysicsShapes().loadPolygon(polygon, 1 / GraphicsManager.PPM), impasse);
							(impasse ? impasseTiles : collisionTiles).add(tile);
						}
					}
					
					/**If none found, adds entire tile collision object.*/
					if(!objects) {
						(impasse ? impasseTiles : collisionTiles)
							.add(new CollisionTile(new Vector2(x + 0.5f, y + 0.5f), 0, Core.getMaps().getTileShape(), impasse));
					}
				}
			}
	}

	public TiledMap getTileMap() {
		return tileMap;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isOutside() {
		return outside;
	}

	public float getMinLight() {
		return minLight;
	}

	public float getMaxLight() {
		return maxLight;
	}

	public int[] getBackground() {
		return background;
	}

	public int[] getForeground() {
		return foreground;
	}

	public Set<CollisionTile> getImpasseTiles() {
		return impasseTiles;
	}

	public Set<CollisionTile> getCollisionTiles() {
		return collisionTiles;
	}

	public Vector<LightSource> getLightSources() {
		return lightSources;
	}

	public ConcurrentHashMap<String, MapObject> getReferenceObjects() {
		return referenceObjects;
	}

	public void setMinLight(float minLight) {
		this.minLight = minLight;
	}

	public void setMaxLight(float maxLight) {
		this.maxLight = maxLight;
	}
}