package net.atlne.dos.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Scene extends Stage {
	
	/**Stores the SpriteBatch for the scene.*/
	protected SpriteBatch batch;
	/**Stores whether the previous scene should be displayed underneath this scene or not.*/
	protected boolean displayUnder;
	
	/**Stores whether the scene has been disposed or not.*/
	protected boolean disposed;
	
	/**Constructor for the Scene class, takes in the core class instance.*/
	public Scene(boolean displayUnder) {
		super(new ScreenViewport(new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())), new SpriteBatch());
		this.batch = (SpriteBatch) getBatch();
		this.displayUnder = displayUnder;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		disposed = true;
	}
	
	/**Updates the camera to fit the viewport every frame.*/
	@Override
	public void draw() {
		getCamera().position.set(getWidth() / 2, getHeight() / 2, 0);
		getCamera().update();
		batch.setProjectionMatrix(getCamera().combined);
		super.draw();
	}
	
	/**Updates the viewport size and the position and scale of actors to match the new screen resolution.*/
	public void resize(int width, int height) {
		getViewport().update(width, height, true);
	}
	
	/**Adds an actor at a given percentage across the screen.*/
	public void addActor(Actor actor, float width, float height) {
		actor.setPosition(getWidth() * (width / 100f), getHeight() * (height / 100f));
		super.addActor(actor);
	}
	
	@Override
	public OrthographicCamera getCamera() {
		return (OrthographicCamera) super.getCamera();
	}
	
	public boolean shouldDisplayUnder() {
		return displayUnder;
	}
	
	public boolean isDisposed() {
		return disposed;
	}
	
	public void setDisplayUnder(boolean displayUnder) {
		this.displayUnder = displayUnder;
	}
}