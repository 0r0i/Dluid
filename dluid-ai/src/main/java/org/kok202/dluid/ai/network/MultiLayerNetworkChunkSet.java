package org.kok202.dluid.ai.network;

import lombok.Builder;
import lombok.Getter;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.util.List;
import java.util.Map;

@Getter
public class MultiLayerNetworkChunkSet {
    private long outputLayerId;
    private Map<Long, MultiLayerNetwork> managersMap;
    private Map<Long, List<Long>> linkageFromTo;

    @Builder
    public MultiLayerNetworkChunkSet(long outputLayerId, Map<Long, MultiLayerNetwork> managersMap, Map<Long, List<Long>> linkageFromTo) {
        this.outputLayerId = outputLayerId;
        this.managersMap = managersMap;
        this.linkageFromTo = linkageFromTo;
    }
}
