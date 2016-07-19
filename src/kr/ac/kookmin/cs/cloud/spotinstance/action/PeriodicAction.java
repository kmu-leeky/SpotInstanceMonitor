package kr.ac.kookmin.cs.cloud.spotinstance.action;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodicAction implements Action {
    private int period;  // period of the action in seconds
    private int initialDelay;
    private int threadPoolCount;

    public PeriodicAction(int period, int initialDelay, int threadPoolCount) {
        this.period = period;
        this.initialDelay = initialDelay;
        this.threadPoolCount = threadPoolCount;
    }

    protected Runnable taskToRun;

    @Override
    public void Start() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(this.threadPoolCount);
        executor.scheduleAtFixedRate(taskToRun, this.initialDelay, this.period, TimeUnit.SECONDS);
    }
}
