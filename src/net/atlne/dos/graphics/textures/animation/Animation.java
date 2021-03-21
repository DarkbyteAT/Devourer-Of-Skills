package net.atlne.dos.graphics.textures.animation;

public class Animation {
	
	/**Stores the duration of the animation in seconds.*/
	protected float duration;
	/**Stores the row, starting index and ending index of the animation.*/
	protected int row, start, end;
	/**Stores whether the animation should be played in reverse.*/
	protected boolean reversed;
	
	/**Constructor for the animation class, takes in the indexes and whether to reverse the animation.*/
	public Animation(float duration, int row, int start, int end, boolean reversed) {
		this.duration = duration;
		this.row = row;
		this.start = start;
		this.end = end;
		this.reversed = reversed;
	}
	
	/**Gets the current frame of the animation given the elapsed time.*/
	public int getCurrentFrame(float elapsedTime) {
		/**Modularly divides the time by the animation duration to clip it cyclically.*/
		/**Then calculates the percentage through the animation that the time is currently at.*/
		float relativeTime = elapsedTime % duration,
				elapsedPercentile = relativeTime / duration;
		/**Calculates the current frame through the */
		/**Rounds the elapsed percentile's product with the animation's width in frames.*/
		/**Gets the width in frames by subtracting the end frame index from the start.*/
		int elapsedFrames = Math.round(elapsedPercentile * (end - start));
		/**Returns the elapsed frames plus the start frame.*/
		/**This gets the frame index that the animation is currently on.*/
		/**Reverses this to be the end frame minus the elapsed frame if reversed.*/
		return reversed ? end - elapsedFrames : start + elapsedFrames;
	}

	public float getDuration() {
		return duration;
	}

	public int getRow() {
		return row;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setDuration(float duration) {
		this.duration = duration;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}
}