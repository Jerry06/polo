package com.polo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static java.lang.Long.parseLong;

/**
 * Created by Viet on 25/01/2018.
 */
public class BinanceTest {

    public static void main(String[] args) throws Exception {
        File resultFile = new File("C:\\result.txt");
        ObjectMapper objectMapper = new ObjectMapper();
        String getAllSymUrl = "https://api.binance.com/api/v1/exchangeInfo";
        List<String> symbols = JsonPath.parse(new URL(getAllSymUrl)).read("symbols[*].symbol");
        List<PumpInfo> pumpInfos = new ArrayList<>();
        for (String symbol : symbols) {
            if (!symbol.contains("BTC")) {
                continue;
            }
            String urlStr = "https://api.binance.com/api/v1/klines?symbol=" + symbol + "&interval=1h&startTime=%s";
            urlStr = String.format(urlStr, ZonedDateTime.now().minusDays(2).toInstant().toEpochMilli());
            final URL url = new URL(urlStr);
            List<List<Object>> list = objectMapper.readValue(url, List.class);
            Queue<Double> queue = new LinkedList<>();
            for (int i = 0; i < 24; i++) {
                queue.add(Double.parseDouble(list.get(i).get(9).toString()));
            }
            int numberOfPumpVol = 0;
            List<PumpInfo.UpPriceInfo> upPriceInfos = new ArrayList<>();
            for (int i = 24; i < list.size(); i++) {
                List<Object> row = list.get(i);
                Instant openTime = Instant.ofEpochMilli(parseLong(row.get(0).toString()));
                Instant closeTime = Instant.ofEpochMilli(parseLong(row.get(6).toString()));
                Double buybaseVol = Double.parseDouble(row.get(9).toString());
                Double totalPre24h = 0D;
                for (Double aDouble : queue) {
                    totalPre24h += aDouble;
                }
                Double avg24h = totalPre24h / 24;
                if (buybaseVol >= avg24h * 1.5) {
                    upPriceInfos.add(new PumpInfo.UpPriceInfo(openTime.atZone(ZoneId.systemDefault()).toString(), buybaseVol / avg24h));
                    numberOfPumpVol++;
//                    System.out.println("Signal at :" + openTime.atZone(ZoneId.systemDefault()));
//                    System.out.println("Percent :" + buybaseVol / avg24h);
                }
                queue.poll();
                queue.add(buybaseVol);
            }
            pumpInfos.add(new PumpInfo(symbol, upPriceInfos));
//            System.out.println("Symbol : " + symbol);
//            System.out.println("numberOfPumpVol : " + numberOfPumpVol);
        }
        Collections.sort(pumpInfos);
        String asString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pumpInfos);
        Files.write(new File("D:\\result.json").toPath(), asString.getBytes());


//            1499040000000,      // Open time 0
//                    "0.01634790",       // Open 1
//                    "0.80000000",       // High 2
//                    "0.01575800",       // Low 3
//                    "0.01577100",       // Close 4
//                    "148976.11427815",  // Volume 5
//                    1499644799999,      // Close time 6
//                    "2434.19055334",    // Quote asset volume 7
//                    308,                // Number of trades 8
//                    "1756.87402397",    // Taker buy base asset volume 9
//                    "28.46694368",      // Taker buy quote asset volume 10
//                    "17928899.62484339" // Ignore 11
    }

}
