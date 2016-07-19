package kr.ac.kookmin.cs.cloud.spotinstance.action.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.SpotPrice;

import kr.ac.kookmin.cs.cloud.spotinstance.action.PeriodicAction;

public class PeriodicPriceChecker extends PeriodicAction {
    private static final String TARGET_INSTANCE = "g2.2xlarge";
    private ConcurrentHashMap<String, SpotPrice> spotInstancePrices;
    private PeriodicRegionsChecker periodicRegionsChecker;

    public PeriodicPriceChecker(int period, int initialDelay, int numberOfThreads) {
        super(period, initialDelay, numberOfThreads);
        spotInstancePrices = new ConcurrentHashMap<String, SpotPrice>();
        periodicRegionsChecker = new PeriodicRegionsChecker();
        periodicRegionsChecker.Start();

        taskToRun = new Runnable() {
            @Override
            public void run() {
                Date currentDate = new Date();
                Date startDate = new Date(currentDate.getTime() - period*1000);
                System.out.println("currentDate: " + currentDate + " startDate: " + startDate);
                System.out.println(spotInstancePrices);
                Iterator<String> regionsEndpoint = periodicRegionsChecker.getRegionsEndpoint();

                while (regionsEndpoint.hasNext()) {
                    String re = regionsEndpoint.next();
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AmazonEC2Client ec2Client = new AmazonEC2Client();
                            ec2Client.setEndpoint(re);
                            DescribeSpotPriceHistoryRequest request = new DescribeSpotPriceHistoryRequest()
                                    .withInstanceTypes(TARGET_INSTANCE).withStartTime(startDate)
                                    .withProductDescriptions("Linux/UNIX");
                            DescribeSpotPriceHistoryResult result = ec2Client.describeSpotPriceHistory(request);
                            List<SpotPrice> history = result.getSpotPriceHistory();
                            for (SpotPrice sp : history) {
                                if (!spotInstancePrices.containsKey(sp.getAvailabilityZone())) {
                                    spotInstancePrices.put(sp.getAvailabilityZone(), sp);
                                } else if (sp.getTimestamp()
                                        .after(spotInstancePrices.get(sp.getAvailabilityZone()).getTimestamp())) {
                                    spotInstancePrices.put(sp.getAvailabilityZone(), sp);
                                }
                            }
                        }
                    });
                    t.start();
                }
            }
        };
    }
    public PeriodicPriceChecker() {
        this(30, 0, 1);
    }
}
