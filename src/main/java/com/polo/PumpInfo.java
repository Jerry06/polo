package com.polo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by Viet on 08/04/2018.
 */
@Data
@AllArgsConstructor
public class PumpInfo {
    private String symbol;
    private List<UpPriceInfo> priceInfos;

    @Data
    @AllArgsConstructor
    public static class UpPriceInfo {
        private String time;
        private double percent;
    }
}
