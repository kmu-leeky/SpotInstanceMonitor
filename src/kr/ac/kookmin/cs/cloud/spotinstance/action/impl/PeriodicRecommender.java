package kr.ac.kookmin.cs.cloud.spotinstance.action.impl;

import java.io.IOException;

import kr.ac.kookmin.cs.cloud.spotinstance.action.PeriodicAction;
import kr.ac.kookmin.cs.cloud.spotinstance.recommender.Recommender;
import kr.ac.kookmin.cs.cloud.spotinstance.recommender.impl.CurrentPriceRecommender;

public class PeriodicRecommender extends PeriodicAction {

    public static final String RECOMMENDER_OUTPUT_PATH = "/tmp/spot_instance_recommend";

    public PeriodicRecommender(int period, int initialDelay, int threadPoolCount) throws IOException {
        super(period, initialDelay, threadPoolCount);
        Recommender recommender = new CurrentPriceRecommender(PeriodicPriceChecker.SPOT_PRICE_OUTPUT_PATH,
                RECOMMENDER_OUTPUT_PATH);

        taskToRun = new Runnable() {
            @Override
            public void run() {
                System.out.println("in the recommender run");
                try {
                    recommender.recommend();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

    public PeriodicRecommender() throws IOException {
        this(30, 60, 1);
    }
}
