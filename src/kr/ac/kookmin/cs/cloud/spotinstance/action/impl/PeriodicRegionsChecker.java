package kr.ac.kookmin.cs.cloud.spotinstance.action.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;

import kr.ac.kookmin.cs.cloud.spotinstance.action.PeriodicAction;

public class PeriodicRegionsChecker extends PeriodicAction {
    private AmazonEC2 ec2Client;
    private volatile Set<String> regionsEndpoint;

    public PeriodicRegionsChecker(int period, int initialDelay, int numberOfThreads) {
        super(period, initialDelay, numberOfThreads);
        ec2Client = new AmazonEC2Client();
        regionsEndpoint = new HashSet<String>();
        taskToRun = new Runnable() {
            @Override
            public void run() {
                Set<String> tempRegions = new HashSet<String>();
                DescribeRegionsResult drs = ec2Client.describeRegions();
                for (Region region : drs.getRegions()) {
                    ec2Client.setEndpoint(region.getEndpoint());
                    System.out.println("region: " + region.getRegionName());
                    tempRegions.add(region.getEndpoint());
                }

                regionsEndpoint = tempRegions;
            }
        };
    }

    public PeriodicRegionsChecker() {
        this(3600, 0, 1);
    }

    public Iterator<String> getRegionsEndpoint() {
        return regionsEndpoint.iterator();
    }
}
