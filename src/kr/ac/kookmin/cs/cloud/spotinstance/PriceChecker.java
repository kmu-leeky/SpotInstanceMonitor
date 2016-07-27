package kr.ac.kookmin.cs.cloud.spotinstance;

import java.io.IOException;

import kr.ac.kookmin.cs.cloud.spotinstance.action.Action;
import kr.ac.kookmin.cs.cloud.spotinstance.action.impl.PeriodicPriceChecker;
import kr.ac.kookmin.cs.cloud.spotinstance.action.impl.PeriodicRecommender;

public class PriceChecker {

    public static void main(String[] args) throws IOException {
        Action getPriceAction = new PeriodicPriceChecker();
        getPriceAction.start();
        Action recommenderAction = new PeriodicRecommender();
        recommenderAction.start();
    }
}
