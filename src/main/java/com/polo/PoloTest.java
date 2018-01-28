package com.polo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Viet on 25/01/2018.
 */
public class PoloTest {

    public static final String ALL_TICKETS_URL = "https://poloniex.com/public?command=returnTicker";
    public static final String ALL_VOLUMNE_24H_URL = "https://poloniex.com/public?command=return24hVolume";
    public static final String CHART_DATA_URL = "https://poloniex.com/public?command=returnChartData&currencyPair=BTC_XMR&start=1405699200&end=9999999999&period=14400";
    public static final String ALL_CURRENCIES_URL = "https://poloniex.com/public?command=returnCurrencies";
//    public static final String ALL_TICKETS_URL = "https://poloniex.com/public?command=returnTicker";

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final URL url = new URL("https://bittrex.com/api/v1.1/public/getmarketsummaries");
        final Map<String, Diff> diffMap = new HashMap<String, Diff>();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("Start get data at :" + LocalDateTime.now());
                    HashMap<String, Object> rootMap = new ObjectMapper().readValue(url, HashMap.class);
                    if (rootMap.get("success").equals(true)) {

                        List<Map<String, Object>> results = (List) rootMap.get("result");
                        for (Map<String, Object> result : results) {
                            Diff diff = diffMap.get(result.get("MarketName").toString());
                            if (diff == null) {
                                diff = new Diff();
                                diff.setLastPrice(Double.parseDouble(result.get("Last").toString()));
                                diff.setLastVolumne(Double.parseDouble(result.get("BaseVolume").toString()));
                                diffMap.put(result.get("MarketName").toString(), diff);
                                continue;
                            }
                            double lastPrice = Double.parseDouble(result.get("Last").toString());

                            diff.setIncreasePrice(lastPrice > diff.getLastPrice());
                            diff.setLastPrice(lastPrice);
                            if (diff.isIncreasePrice()) {
                                diff.setNumberTimesOfIncreasePrice(diff.getNumberTimesOfIncreasePrice() + 1);
                            } else {
                                diff.setNumberTimesOfIncreasePrice(0);
                            }
                            double lastVolumne = Double.parseDouble(result.get("BaseVolume").toString());
                            double newDiffVolumne = lastVolumne - diff.getLastVolumne();

//                            if (diff.getDiffVolumne() > 0 && newDiffVolumne > diff.getDiffVolumne() * 4
//                                    && diff.isIncreasePrice()) {
//                                System.out.println(result.get("MarketName") + "pump pump 2.2");
//                                diff.setNumberTimesOfIncreasePrice(0);
//                                diff.setNumberTimesOfIncreaseBigVolumne(0);
//                                diffMap.put(result.get("MarketName").toString(), diff);
//                                return;
//                            }

                            if (newDiffVolumne > diff.getDiffVolumne() * 1.2) {
                                diff.setNumberTimesOfIncreaseBigVolumne(diff.getNumberTimesOfIncreaseBigVolumne() + 1);
                            }
                            diff.setDiffVolumne(newDiffVolumne);
                            diff.setLastVolumne(lastVolumne);

                            if (diff.getNumberTimesOfIncreasePrice() >= 4 && diff.getNumberTimesOfIncreaseBigVolumne() >= 3) {
                                System.out.println(result.get("MarketName") + "pump pump");
                                diff.setNumberTimesOfIncreasePrice(0);
                                diff.setNumberTimesOfIncreaseBigVolumne(0);
                                diffMap.put(result.get("MarketName").toString(), diff);
                                return;
                            } else {
                                diffMap.put(result.get("MarketName").toString(), diff);
                            }
                        }
                    } else {
                        System.out.println("disconnect");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 60000);

    }
}
