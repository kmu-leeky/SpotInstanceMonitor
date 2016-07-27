package kr.ac.kookmin.cs.cloud.spotinstance.recommender;

import java.io.IOException;

public interface Recommender {
    public String recommend() throws IOException;

    public String recommend(String currentAz) throws IOException;
}
