package kr.ac.kookmin.cs.cloud.spotinstance.action;

import java.io.IOException;
import java.util.Map;

public interface RecordReader {
    public Map<String, Float> readRecentSpotPrice() throws IOException;
}
