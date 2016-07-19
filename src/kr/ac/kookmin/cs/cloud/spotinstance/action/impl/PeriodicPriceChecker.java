package kr.ac.kookmin.cs.cloud.spotinstance.action.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.SpotPrice;

import kr.ac.kookmin.cs.cloud.spotinstance.action.PeriodicAction;
import kr.ac.kookmin.cs.cloud.spotinstance.model.SpotInstancePrice;

public class PeriodicPriceChecker extends PeriodicAction {
    private static final String TARGET_INSTANCE = "g2.2xlarge";
    private Set<SpotInstancePrice> spotInstancePrices;
    private PeriodicRegionsChecker periodicRegionsChecker;

    public PeriodicPriceChecker(int period, int initialDelay, int numberOfThreads) {
        super(period, initialDelay, numberOfThreads);
        spotInstancePrices = new HashSet<SpotInstancePrice>();
        periodicRegionsChecker = new PeriodicRegionsChecker();
        periodicRegionsChecker.Start();

        taskToRun = new Runnable() {
            @Override
            public void run() {
                Date currentDate = new Date();
                Date startDate = new Date(currentDate.getTime() - period*1000);
                System.out.println("currentDate: " + currentDate + " startDate: " + startDate);
                Iterator<String> regionsEndpoint = periodicRegionsChecker.getRegionsEndpoint();

                while (regionsEndpoint.hasNext()) {
                    String re = regionsEndpoint.next();
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AmazonEC2Client ec2Client = new AmazonEC2Client();
                            ec2Client.setEndpoint(re);
                            DescribeSpotPriceHistoryRequest request = new DescribeSpotPriceHistoryRequest()
                                    .withInstanceTypes(TARGET_INSTANCE).withStartTime(startDate);
                            DescribeSpotPriceHistoryResult result = ec2Client.describeSpotPriceHistory(request);
                            List<SpotPrice> history = result.getSpotPriceHistory();
                            for (SpotPrice sp : history) {
                                System.out.println(sp.getTimestamp() + "\t" + sp.getAvailabilityZone() + "\t" + sp.getSpotPrice());
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
