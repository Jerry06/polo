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
        JsonNode actualObj = mapper.readTree(" {\n" +
                "\t\"success\" : true,\n" +
                "\t\"message\" : \"\",\n" +
                "\t\"rootMap\" : [{\n" +
                "\t\t\t\"MarketName\" : \"BTC-888\",\n" +
                "\t\t\t\"High\" : 0.00000919,\n" +
                "\t\t\t\"Low\" : 0.00000820,\n" +
                "\t\t\t\"Volume\" : 74339.61396015,\n" +
                "\t\t\t\"Last\" : 0.00000820,\n" +
                "\t\t\t\"BaseVolume\" : 0.64966963,\n" +
                "\t\t\t\"TimeStamp\" : \"2014-07-09T07:19:30.15\",\n" +
                "\t\t\t\"Bid\" : 0.00000820,\n" +
                "\t\t\t\"Ask\" : 0.00000831,\n" +
                "\t\t\t\"OpenBuyOrders\" : 15,\n" +
                "\t\t\t\"OpenSellOrders\" : 15,\n" +
                "\t\t\t\"PrevDay\" : 0.00000821,\n" +
                "\t\t\t\"Created\" : \"2014-03-20T06:00:00\",\n" +
                "\t\t\t\"DisplayMarketName\" : null\n" +
                "\t\t}, {\n" +
                "\t\t\t\"MarketName\" : \"BTC-A3C\",\n" +
                "\t\t\t\"High\" : 0.00000072,\n" +
                "\t\t\t\"Low\" : 0.00000001,\n" +
                "\t\t\t\"Volume\" : 166340678.42280999,\n" +
                "\t\t\t\"Last\" : 0.00000005,\n" +
                "\t\t\t\"BaseVolume\" : 17.59720424,\n" +
                "\t\t\t\"TimeStamp\" : \"2014-07-09T07:21:40.51\",\n" +
                "\t\t\t\"Bid\" : 0.00000004,\n" +
                "\t\t\t\"Ask\" : 0.00000005,\n" +
                "\t\t\t\"OpenBuyOrders\" : 18,\n" +
                "\t\t\t\"OpenSellOrders\" : 18,\n" +
                "\t\t\t\"PrevDay\" : 0.00000002,\n" +
                "\t\t\t\"Created\" : \"2014-05-30T07:57:49.637\",\n" +
                "\t\t\t\"DisplayMarketName\" : null\n" +
                "\t\t}\n" +
                "    ]\n" +
                "}");
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
                                diff.setNumberTimesOfIncreasePrice(diff.getNumberTimesOfIncreasePrice() - 1);
                            }
                            double lastVolumne = Double.parseDouble(result.get("BaseVolume").toString());
                            double newDiffVolumne = lastVolumne - diff.getLastVolumne();
                            if (newDiffVolumne > diff.getDiffVolumne() * 2) {
                                diff.setNumberTimesOfIncreaseBigVolumne(diff.getNumberTimesOfIncreaseBigVolumne() + 1);
                            }
                            diff.setDiffVolumne(newDiffVolumne);
                            diff.setLastVolumne(lastVolumne);

                            if (diff.getNumberTimesOfIncreasePrice() >= 5 && diff.getNumberTimesOfIncreaseBigVolumne() >= 5) {
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
