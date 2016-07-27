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
    private volatile Set<Region> regionsEndpoint;

    public PeriodicRegionsChecker(int period, int initialDelay, int numberOfThreads) {
        super(period, initialDelay, numberOfThreads);
        ec2Client = new AmazonEC2Client();
        regionsEndpoint = new HashSet<Region>();
        taskToRun = new Runnable() {
            @Override
            public void run() {
                Set<Region> tempRegions = new HashSet<Region>();
                DescribeRegionsResult drs = ec2Client.describeRegions();
                for (Region region : drs.getRegions()) {
                    ec2Client.setEndpoint(region.getEndpoint());
                    tempRegions.add(region);
                }

                regionsEndpoint = tempRegions;
                System.out.println("Done for fill the Regions");
            }
        };
    }

    public PeriodicRegionsChecker() {
        this(3600, 0, 1);
    }

    public Iterator<Region> getRegions() {
        return regionsEndpoint.iterator();
    }
}
