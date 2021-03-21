package net.atlne.dos.utils.timing;

import com.badlogic.gdx.Gdx;

public class SecondStopwatch implements Stopwatch<Float> {
	
	/**Stores the starting time for the Stopwatch.*/
	private float start;
	/**Stores the current time.*/
	private float current;

	/**Starts the stopwatch.*/
	public void record() {
		start = current;
	}
	
	/**Stops the stopwatch and returns the time since the start.*/
	public Float check() {
		current += Gdx.graphics.getDeltaTime();
		return current - start;
	}
}