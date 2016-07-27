package kr.ac.kookmin.cs.cloud.spotinstance.action.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryRequest;
import com.amazonaws.services.ec2.model.DescribeSpotPriceHistoryResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.SpotPrice;

import kr.ac.kookmin.cs.cloud.spotinstance.action.PeriodicAction;
import kr.ac.kookmin.cs.cloud.spotinstance.action.RecordWriter;

public class PeriodicPriceChecker extends PeriodicAction {
    public static final String SPOT_PRICE_OUTPUT_PATH = "/tmp/current_spot_prices";
    private static final String TARGET_INSTANCE = "g2.2xlarge";
    private ConcurrentHashMap<String, SpotPrice> spotInstancePrices;
    private PeriodicRegionsChecker periodicRegionsChecker;
    private final RecordWriter priceWriter;

    public PeriodicPriceChecker(int period, int initialDelay, int numberOfThreads) {
        super(period, initialDelay, numberOfThreads);
        priceWriter = new FileRecordWriter(SPOT_PRICE_OUTPUT_PATH);
        spotInstancePrices = new ConcurrentHashMap<String, SpotPrice>();
        periodicRegionsChecker = new PeriodicRegionsChecker();
        periodicRegionsChecker.start();

        taskToRun = new Runnable() {
            @Override
            public void run() {
                Date currentDate = new Date();
                Date startDate = new Date(currentDate.getTime() - period*1000);
                System.out.println("currentDate: " + currentDate + " startDate: " + startDate);
                System.out.println(spotInstancePrices);
                Iterator<Region> regions = periodicRegionsChecker.getRegions();
                ExecutorService es = Executors.newCachedThreadPool();

                while (regions.hasNext()) {
                    Region region = regions.next();
                    es.execute(new Runnable() {
                        @Override
                        public void run() {
                            AmazonEC2Client ec2Client = new AmazonEC2Client();
                            ec2Client.setEndpoint(region.getEndpoint());
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
                }
                es.shutdown();
                try {
                    es.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    System.out.println("Wait is interrupted");
                }
                StringBuilder sb = new StringBuilder();
                for (String k : spotInstancePrices.keySet()) {
                    SpotPrice sp = spotInstancePrices.get(k);
                    sb.append(k).append("\t").append(sp.getSpotPrice()).append("\n");
                }

                priceWriter.write(sb.toString());
            }
        };
    }
    public PeriodicPriceChecker() {
        this(30, 0, 1);
    }
}
