package kr.ac.kookmin.cs.cloud.spotinstance.recommender.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.ac.kookmin.cs.cloud.spotinstance.action.RecordReader;
import kr.ac.kookmin.cs.cloud.spotinstance.action.RecordWriter;
import kr.ac.kookmin.cs.cloud.spotinstance.action.impl.FileRecordReader;
import kr.ac.kookmin.cs.cloud.spotinstance.action.impl.FileRecordWriter;
import kr.ac.kookmin.cs.cloud.spotinstance.recommender.Recommender;

public class CurrentPriceRecommender implements Recommender {

    RecordReader recordReader;
    RecordWriter recommendWriter;

    public CurrentPriceRecommender(String priceRecordLocation, String recommendLocation) throws FileNotFoundException {
        recordReader = new FileRecordReader(priceRecordLocation);
        recommendWriter = new FileRecordWriter(recommendLocation);
    }

    @Override
    public String recommend() throws IOException {
        return recommend(null);
    }

    @Override
    public String recommend(String currentAz) throws IOException {
        Map<String, Float> spotPrices = recordReader.readRecentSpotPrice();
        List<String> candidateAzs = new ArrayList<String>();
        float minPrice = Float.MAX_VALUE;
        for (String k : spotPrices.keySet()) {
            float azPrice = spotPrices.get(k);
            if (azPrice > minPrice) {
                continue;
            } else if (azPrice == minPrice) {
                candidateAzs.add(k);
            } else {
                minPrice = azPrice;
                candidateAzs.clear();
                candidateAzs.add(k);
            }
        }
        String recommendedAz = (currentAz != null && candidateAzs.contains(currentAz)) ? currentAz
                : candidateAzs.get(0);
        recommendWriter.write(recommendedAz);
        return recommendedAz;
    }

}
