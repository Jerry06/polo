package com.polo;

import lombok.Data;

/**
 * Created by Viet on 25/01/2018.
 */
@Data
public class Diff {

    private String id;
    private double lastPrice;
    private double diffPrice;
    private double diffVolumne;
    private double lastVolumne;
    private boolean isIncreaseBigVolumne;
    private boolean isIncreasePrice;
    private int numberTimesOfIncreasePrice;
    private int numberTimesOfIncreaseBigVolumne;
}
