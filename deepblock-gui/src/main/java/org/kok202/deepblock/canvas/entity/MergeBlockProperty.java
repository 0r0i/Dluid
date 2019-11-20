package org.kok202.deepblock.canvas.entity;

import lombok.Data;

@Data
public class MergeBlockProperty extends SkewedBlockProperty{
    private int pointingIndex;

    public int getPointingIndex(int excludeMaxSize){
        pointingIndex = Math.max(pointingIndex, 0);
        pointingIndex = Math.min(pointingIndex, excludeMaxSize - 1);
        return pointingIndex;
    }
}
