package net.atlne.dos.game.maps;

import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Disposable;

public class MapLoader implements Disposable {
	
	/**Stores the rectangle shape for collision and impasse tiles.*/
	private PolygonShape tileShape;
	/**Stores the map loader for loading in the tile maps in TMX format.*/
	private TmxMapLoader mapLoader = new TmxMapLoader(new LocalFileHandleResolver());
	/**Stores the loaded maps for the game.*/
	private ConcurrentHashMap<String, Map> maps = new ConcurrentHashMap<String, Map>();
	
	public MapLoader() {
		tileShape = new PolygonShape();
		tileShape.setAsBox(0.5f, 0.5f);
	}
	
	/**Disposes of all of the loaded maps.*/
	public void dispose() {
		for(Map map : maps.values())
			map.dispose();
		maps.clear();
		tileShape.dispose();
	}

	/**Loads a map into the game, or gets the map from the HashMap if already loaded, and returns it.*/
	public Map get(String mapName) {
		if(!maps.containsKey(mapName))
			maps.put(mapName, new Map(mapLoader.load("assets/maps/" + mapName + ".tmx")));
		return maps.get(mapName);
	}
	
	/**Gets the tile shape for rectangular tiles.*/
	public PolygonShape getTileShape() {
		return tileShape;
	}
}