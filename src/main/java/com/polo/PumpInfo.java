package com.polo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Viet on 08/04/2018.
 */
@Data
@AllArgsConstructor
public class PumpInfo implements Comparable<PumpInfo> {
    private String symbol;
    private List<UpPriceInfo> priceInfos = new ArrayList<>();

    @Override
    public int compareTo(PumpInfo o) {
        return this.priceInfos.size() - o.priceInfos.size();
    }

    @Data
    @AllArgsConstructor
    public static class UpPriceInfo {
        private String time;
        private double percent;
    }
}
