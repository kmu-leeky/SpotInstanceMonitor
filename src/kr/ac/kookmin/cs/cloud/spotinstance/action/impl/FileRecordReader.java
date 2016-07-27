package kr.ac.kookmin.cs.cloud.spotinstance.action.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import kr.ac.kookmin.cs.cloud.spotinstance.action.RecordReader;

public class FileRecordReader implements RecordReader {
    private final String recordLocation;

    public FileRecordReader(String recordLocation) throws FileNotFoundException {
        this.recordLocation = recordLocation;
    }

    @Override
    public Map<String, Float> readRecentSpotPrice() throws IOException {
        Map<String, Float> spotPrices = new HashMap<String, Float>();
        System.out.println("file record location: " + recordLocation);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(recordLocation))){
            String currentLine = fileReader.readLine();
            while (currentLine != null) {
                String[] splits = currentLine.split("\t");
                spotPrices.put(splits[0], Float.valueOf(splits[1]));
                currentLine = fileReader.readLine();
            }
        }

        return spotPrices;
    }

}
