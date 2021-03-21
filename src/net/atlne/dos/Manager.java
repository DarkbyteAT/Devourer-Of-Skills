package net.atlne.dos;

import com.badlogic.gdx.utils.Disposable;

public abstract class Manager implements Disposable {

	/**Instance of the Core class.*/
	protected Core core;
	
	/**Constructor for the Manager, takes in Core class instance.*/
	public Manager(Core core) {
		this.core = core;
	}
	
	/**Getter for Core class instance.*/
	public Core getCore() {
		return core;
	}
}