package kr.ac.kookmin.cs.cloud.spotinstance.action.impl;

import kr.ac.kookmin.cs.cloud.spotinstance.action.PeriodicAction;

public class AzRecommender extends PeriodicAction {

    public AzRecommender(int period, int initialDelay, int threadPoolCount) {
        super(period, initialDelay, threadPoolCount);
    }

    public AzRecommender() {
        this(30, 60, 1);
    }
}
