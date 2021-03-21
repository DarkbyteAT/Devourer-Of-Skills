package net.atlne.dos.utils.timing;

public class NanoStopwatch implements Stopwatch<Long> {
	
	/**Stores the starting time for the Stopwatch.*/
	private long start;

	/**Starts the stopwatch.*/
	public void record() {
		start = System.nanoTime();
	}
	
	/**Stops the stopwatch and returns the since the start.*/
	public Long check() {
		return System.nanoTime() - start;
	}
	
	/**Checks the timer and re-records.*/
	public Long checkRecord() {
		long check = check();
		record();
		return check;
	}
}