package net.atlne.dos.utils.debug;

public class HADebug {
	
	public static long getTotalMemory() {
	    return Runtime.getRuntime().totalMemory();
	}
	
	public static long getAllocatedMemory() {
	    return Runtime.getRuntime().maxMemory();
	}

	public static long getFreeMemory() {
	    return Runtime.getRuntime().freeMemory();
	}
	
	public static long getUsedMemory() {
	    return getTotalMemory() - getFreeMemory();
	}
	
	public static float getUsedMemoryProportion() {
		return (float) (getUsedMemory() / getAllocatedMemory());
	}
}