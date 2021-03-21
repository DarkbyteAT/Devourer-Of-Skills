package net.atlne.dos.maps;

import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import net.atlne.dos.Core;
import net.atlne.dos.entities.Entity;
import net.atlne.dos.graphics.GraphicsManager;
import net.atlne.dos.maps.objects.CollisionTile;
import net.atlne.dos.maps.objects.LightSource;
import net.atlne.dos.utils.maths.HAMaths.TrigonometryUtils;
import net.atlne.dos.utils.timing.NanoStopwatch;
import net.atlne.dos.utils.timing.SecondStopwatch;

public class Room {
	
	/**Stores the fraction of the window 1 tile should make up.*/
	public static final float ABSOLUTE_TILE_SIZE = 500f;
	/**Stores the default physics tick rate for the game.*/
	public static final float PHYSICS_TICK_RATE = 1f / 300f;
	/**Stores the day length for rooms in real-time seconds.*/
	public static final float DAY_LENGTH = 360;
	
	/**Stores the colour to draw grid lines in debug mode.*/
	public static final Color GRID_COLOUR = new Color(0.3f, 0.3f, 0.3f, 0.3f);
	
	/**Stores the map for the room.*/
	protected Map map;
	/**Stores the renderer for the tile map.*/
	protected OrthogonalTiledMapRenderer mapRenderer;
	/**Stores the OrthographicCamera for the room's current focus.*/
	protected OrthographicCamera camera;
	/**Stores the position of the camera in world space coordinates.*/
	protected Vector2 cameraPos;
	/**Stores the zoom level for the camera, lower values mean more zoomed in.*/
	protected float zoom = 0.25f;
	/**Stores the ShapeRenderer for rendering the grid in debug mode.*/
	protected ShapeRenderer gridRenderer;
	/**Stores the Box2DDebugRenderer for rendering collision outlines in debug mode.*/
	protected Box2DDebugRenderer debugRenderer;
	/**Stores the Box2D environment for the room.*/
	protected World world;
	/**Stores the entities in the room, mapped to their IDs.*/
	protected ConcurrentHashMap<String, Entity> entities = new ConcurrentHashMap<>();
	/**Stores the RayHandler for the world for rendering the lights.*/
	protected RayHandler rayHandler;
	/**Stores the baked light-sources in the room, mapped to the light source data for the lights.*/
	protected ConcurrentHashMap<PointLight, LightSource> lights = new ConcurrentHashMap<>();
	
	/**Stores the tick timer.*/
	protected SecondStopwatch tickTimer = new SecondStopwatch();
	/**Stores the time for the room in seconds.*/
	protected float time;
	/**Stores the ambient light level of the room.*/
	protected float ambientLight;
	
	/**Stores the last 10 TPS measurements.*/
	protected int tps;
	/**Stores a timer to count 1 second.*/
	protected NanoStopwatch tpsTimer = new NanoStopwatch();
	/**Stores the number of ticks counted in a second.*/
	protected int tpsCount;

	public Room(String mapName) {
		this.map = Core.maps.get(mapName);
		this.mapRenderer = new OrthogonalTiledMapRenderer(map.getTileMap());
		this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.cameraPos = new Vector2();
		this.rayHandler = new RayHandler(world);
		this.gridRenderer = new ShapeRenderer();
		this.debugRenderer = new Box2DDebugRenderer();
		this.world = new World(new Vector2(0, 0), false);
		world.setContinuousPhysics(true);
		
		for(CollisionTile tile : map.getImpasseTiles())
			tile.createBody(world);
		for(CollisionTile tile : map.getCollisionTiles())
			tile.createBody(world);
		for(LightSource light : map.getLightSources())
			lights.put(light.generate(this), light);
		
		rayHandler.setCulling(true);
		rayHandler.setShadows(true);
		rayHandler.setBlur(true);
		rayHandler.setBlurNum(2);
		
		tpsTimer.record();
	}
	
	public void dispose() {
		mapRenderer.dispose();
		rayHandler.dispose();
		gridRenderer.dispose();
		debugRenderer.dispose();
		world.dispose();
	}
	
	public void draw(SpriteBatch batch) {
		/**Updates the batch to work with the rendering the map.*/
		Matrix4 originalProj = batch.getProjectionMatrix().cpy();
		camera.position.set(cameraPos.cpy().scl(GraphicsManager.PPM), 0);
		camera.zoom = zoom;
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.end();
		
		/**Renders the background layers of the map.*/
		mapRenderer.setView(camera);
		mapRenderer.render(map.getBackground());
		
		/**Draws the entities in the room.*/
		batch.begin();
		/**Iterates over every game entity.
		 * Sorts them in reverse order by their draw order (defaulted to y-ordering).
		 * Therefore objects at the bottom of the screen (or have artificially-lowered draw orders) are drawn first.*/
		for(Object o : entities.values()
						.stream()
						.sorted((a, b) -> b.drawOrder() > a.drawOrder() ? 1 :
							b.drawOrder() < a.drawOrder() ? -1 : 0)
						.toArray())
			((Entity) o).draw(batch);
		batch.end();
		
		/**Draws the foreground layers of the map and handles lighting.*/
		mapRenderer.render(map.getForeground());
		rayHandler.setAmbientLight(ambientLight);
		rayHandler.setCombinedMatrix(camera);
		rayHandler.updateAndRender();
		
		/**Draws the hitboxes and grid if the debug overlay is open.*/
		Vector3 originalCameraPos = camera.position.cpy();
		camera.position.set(cameraPos.cpy(), 0);
		camera.zoom /= GraphicsManager.PPM;
		camera.update();
		drawGrid();
		debugRenderer.render(world, camera.combined);
		camera.zoom *= GraphicsManager.PPM;
		camera.position.set(originalCameraPos);
		camera.update();
		
		/**Resets the parameters to continue rendering on the batch normally.*/
		camera.zoom = zoom;
		batch.setProjectionMatrix(originalProj.cpy());
		batch.begin();
	}
	
	/**Draws the grid for the map in debug mode.*/
	public void drawGrid() {
		int width = map.getWidth(),
			height = map.getHeight();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		gridRenderer.setProjectionMatrix(camera.combined);
		gridRenderer.setColor(GRID_COLOUR);
		gridRenderer.begin(ShapeType.Line);	
		/**Draws vertical grid lines.*/
		for(int x = 0; x <= width; x++)
			gridRenderer.line(x, 0, x, height);
		/**Draws horizontal grid lines.*/
		for(int y = 0; y <= height; y++)
			gridRenderer.line(0, y, width, y);
		gridRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public void update() {
		/**Updates the time.*/
		time = time + Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
		/**Updates the light level for dimming light sources based on the ambient light level.*/
		this.ambientLight = getAmbientLight();
		for(PointLight light : lights.keySet()) {
			LightSource source = lights.get(light);
			light.setDistance(source.getRealBrightness());
			if(source.isDimming())
				light.setDistance(light.getDistance() * Math.max(map.getMinLight(), map.getMaxLight() - ambientLight));
			if(source.getFlicker() > 0)
				light.setDistance(light.getDistance() + (source.getRealFlicker() * TrigonometryUtils.cos(source.getFlickerSpeed() * time)));
		}
		
		/**Updates the physics step if necessary.*/
		float tickTime = tickTimer.check();
		
		if(tickTime >= PHYSICS_TICK_RATE) {
			world.step(PHYSICS_TICK_RATE * (tickTime / PHYSICS_TICK_RATE), 6, 2);
			/**Updates the entities.*/
			for(Entity e : entities.values())
				e.update();
			tickTimer.record();
			tpsCount++;
		}
		
		/**Updates the TPS list if 1 second has passed.*/
		if(tpsTimer.check() >= 1000000000L) {
			tps = tpsCount;
			tpsCount = 0;
			tpsTimer.record();
		}
		
		/**Adds measurements to debug menu if open.*/
		if(Core.debug.isActive()) {
			Core.debug.getDebugPanes().put("TPS", Integer.toString(tps));
			Core.debug.getDebugPanes().put("Camera Zoom", Float.toString(camera.zoom));
			Core.debug.getDebugPanes().put("In-Game Time", Float.toString(time));
			Core.debug.getDebugPanes().put("Ambient Light", Float.toString(ambientLight));
		}
	}
	
	public void resize(int width, int height) {
		camera.setToOrtho(false, width, height);
	}
	
	/**Calculates the ambient light level from the time.
	 * Graph: https://www.desmos.com/calculator/ebobesjjac*/
	public float getAmbientLight() {
		return (float) Math.min(map.getMaxLight(),
							Math.max(map.getMinLight(),
									2 * map.getMaxLight() * TrigonometryUtils.cos((MathUtils.PI * time) / (DAY_LENGTH / 2))));
	}

	public Map getMap() {
		return map;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public Vector2 getCameraPos() {
		return cameraPos;
	}

	public World getWorld() {
		return world;
	}

	public ConcurrentHashMap<String, Entity> getEntities() {
		return entities;
	}
	
	public RayHandler getRayHandler() {
		return rayHandler;
	}

	public ConcurrentHashMap<PointLight, LightSource> getLights() {
		return lights;
	}

	public float getTime() {
		return time;
	}

	public void setCameraPos(Vector2 cameraPos) {
		this.cameraPos = cameraPos;
	}

	public void setTime(float time) {
		this.time = time;
	}
}