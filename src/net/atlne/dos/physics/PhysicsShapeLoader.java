package net.atlne.dos.physics;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import aurelienribon.bodyeditor.BodyEditorLoader;
import net.atlne.dos.utils.files.FileHandler;
import net.atlne.dos.utils.physics.HAPhysics;

public class PhysicsShapeLoader {
	
	/**Stores a list of all of the physics body loaders to load.*/
	private Queue<File> toLoad = new LinkedList<File>();
	/**Stores a map of all of the loaded physics body loaders with their file names. <br>
	 * The map key for an example file <em>test.json</em> would be the string <em>test</em>.*/
	private ConcurrentHashMap<String, BodyEditorLoader> loaders = new ConcurrentHashMap<String, BodyEditorLoader>();
	/**Stores a map of the loaded rectangle physics shapes mapped to their dimensions (width / height to x / y).*/
	private HashMap<Vector2, PolygonShape> rectangles = new HashMap<Vector2, PolygonShape>();
	/**Stores a map of the loaded polygon physics shapes mapped to their original polygons.*/
	private HashMap<Polygon, PolygonShape> polygons = new HashMap<Polygon, PolygonShape>();
	
	public PhysicsShapeLoader() {
		for(File loaderFile : FileHandler.getAllFilesInFolderWithExtension(new File("assets/physics"), "json"))
			toLoad.add(loaderFile);
	}
	
	public void dispose() {
		for(PolygonShape rectangle : rectangles.values())
			rectangle.dispose();
		for(PolygonShape polygon : polygons.values())
			polygon.dispose();
	}
	
	/**Initialises the body editor loaders for the game, returns true if all body editor loaders have been loaded.*/
	public boolean generate() {
		if(!toLoad.isEmpty()) {
			File loaderFile = toLoad.poll();
			loaders.put(loaderFile.getName().substring(0, loaderFile.getName().lastIndexOf('.')).toLowerCase(),
					new BodyEditorLoader(new FileHandle(loaderFile)));
		}
		
		return toLoad.isEmpty();
	}
	
	/**Loads a shape into a fixture and returns the origin position relative to the size.*/
	public void loadFixtureShape(PhysicsObject obj, String loaderName, String shapeName, float sizeInMetres) {
		BodyEditorLoader loader = loaders.get(loaderName);
		loader.attachFixture(obj.getBody(), shapeName, obj.getFixtureDef(), sizeInMetres);
		obj.setOrigin(loader.getOrigin(shapeName, sizeInMetres).cpy());
	}
	
	/**Returns or creates and returns a PolygonShape of a rectangle with the given size.*/
	public PolygonShape loadRectangle(Vector2 size) {
		if(!rectangles.containsKey(size))
			rectangles.put(size, HAPhysics.createRectangleShape(size));
		return rectangles.get(size);
	}
	
	/**Returns or creates and returns a PolygonShape of the given polygon.*/
	public PolygonShape loadPolygon(Polygon polygon) {
		if(!polygons.containsKey(polygon)) {
			PolygonShape polygonShape = new PolygonShape();
			polygonShape.set(polygon.getVertices());
			polygons.put(polygon, polygonShape);
		}
		
		return polygons.get(polygon);
	}
	
	/**Returns or creates and returns a PolygonShape of the given polygon, scaled down by the given factor.*/
	public PolygonShape loadPolygon(Polygon polygon, float scale) {
		if(!polygons.containsKey(polygon)) {
			float[] scaledVertices = new float[polygon.getVertices().length];
			for(int i = 0; i < scaledVertices.length; i++)
				scaledVertices[i] = polygon.getVertices()[i] * scale;
			PolygonShape polygonShape = new PolygonShape();
			polygonShape.set(scaledVertices);
			polygons.put(polygon, polygonShape);
		}
		
		return polygons.get(polygon);
	}
}