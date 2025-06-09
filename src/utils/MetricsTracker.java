package utils;

public class MetricsTracker {
    private long startTime;
    private long endTime;
    private int diskAccesses;

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void endTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public void resetDiskAccesses() {
        diskAccesses = 0;
    }

    public void incrementDiskAccess() {
        diskAccesses++;
    }

    public int getDiskAccesses() {
        return diskAccesses;
    }

    public long getUsedMemory() {
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        return rt.totalMemory() - rt.freeMemory();
    }
}
