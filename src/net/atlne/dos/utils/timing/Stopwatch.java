package net.atlne.dos.utils.timing;

public interface Stopwatch<T> {
	void record();
	T check();
}