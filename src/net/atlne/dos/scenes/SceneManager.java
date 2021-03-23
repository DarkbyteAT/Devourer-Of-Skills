package net.atlne.dos.scenes;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import net.atlne.dos.Core;

public class SceneManager implements Disposable {
	
	/**Stores the scenes active in the game in a stack.*/
	private Stack<Scene> scenes = new Stack<Scene>();
	/**Stores the queue of scenes to be pushed onto the stack after updating.*/
	private Queue<Scene> pushQueue = new LinkedList<Scene>();
	/**Stores the queue of scenes to be popped from the stack after updating.*/
	private Queue<Scene> popQueue = new LinkedList<Scene>();
	/**Stores the foreground scene used for displaying errors.*/
	private Scene errorScene;

	public SceneManager() {
		errorScene = new Scene(true);
	}

	/**Disposes of all scenes in the stack as well as the error scene.*/
	@Override
	public void dispose() {
		while(!scenes.isEmpty())
			scenes.pop().dispose();
		errorScene.dispose();
	}
	
	/**Updates and renders all of the appropriate scenes and manages input handling for them.*/
	public void update() {
		/**Pushes all queued scenes to the stack.*/
		while(!pushQueue.isEmpty())
			scenes.push(pushQueue.remove());
		
		/**Pops all queued scenes from the stack.*/
		while(!popQueue.isEmpty()) {
			popQueue.peek().dispose();
			scenes.remove(popQueue.remove());
		}
		
		Stack<Scene> tempStack = new Stack<Scene>();
		boolean errorSceneActive = errorScene.getActors().size > 0;
		
		/**Updates the error scene and passes input handling if necessary.
		 * Else, passes input handling to the top scene.*/
		if(errorSceneActive) {
			Gdx.input.setInputProcessor(errorScene);
			errorScene.getBatch().setProjectionMatrix(errorScene.getCamera().combined);
			errorScene.getCamera().update();
			errorScene.act();
		} else if(!scenes.isEmpty()) {
			Gdx.input.setInputProcessor(scenes.peek());
		}
		
		/**Gathers all scenes to be displayed.*/
		if(!scenes.isEmpty()) {
			tempStack.push(scenes.peek());
			for(int i = scenes.size() - 2; i >= 0 && scenes.get(i).shouldDisplayUnder(); i--)
				tempStack.push(scenes.get(i));
		}
		
		/**Renders and handles logic for all scenes to be displayed.*/
		while(!tempStack.isEmpty()) {
			Scene scene = tempStack.pop();
			scene.getBatch().setProjectionMatrix(scene.getCamera().combined);
			scene.getCamera().update();
			Core.getInput().setInputScene(scene);
			scene.act();
			scene.draw();
		}
		
		/**Renders the error scene if necessary.*/
		if(errorSceneActive)
			errorScene.draw();
		/**Resets input polling restrictions.*/
		Core.getInput().resetInputScene();
	}
	
	/**Resizes all scenes in the game.*/
	public void resize(int width, int height) {
		for(Scene scene : scenes)
			scene.resize(width, height);
		errorScene.resize(width, height);
	}
	
	public void pushScene(Scene scene) {
		pushQueue.add(scene);
	}
	
	public void popScene(Scene scene) {
		popQueue.add(scene);
	}
	
	public void popScene() {
		if(!scenes.isEmpty())
			popQueue.add(scenes.peek());
	}
	
	public Scene peekScene() {
		return scenes.peek();
	}
	
	public Scene peekScene(Class<?> scene) {
		Scene foundScene = scenes.stream()
				.filter(s -> s.getClass().equals(scene))
				.findFirst()
				.orElse(null);
		return foundScene;
	}

	public Stack<Scene> getSceneStack() {
		return scenes;
	}

	public Stage getErrorScene() {
		return errorScene;
	}
}