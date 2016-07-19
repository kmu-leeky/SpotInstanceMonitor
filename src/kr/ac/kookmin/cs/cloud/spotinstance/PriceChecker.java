package kr.ac.kookmin.cs.cloud.spotinstance;

import kr.ac.kookmin.cs.cloud.spotinstance.action.Action;
import kr.ac.kookmin.cs.cloud.spotinstance.action.impl.PeriodicPriceChecker;

public class PriceChecker {

    public static void main(String[] args) {
        Action getPriceAction = new PeriodicPriceChecker();
        getPriceAction.Start();
    }
}
