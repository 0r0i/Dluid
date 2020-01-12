package org.kok202.dluid.ai.network.layer.binder;

import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.layers.BaseLayer.Builder;
import org.kok202.dluid.ai.entity.Layer;
import org.kok202.dluid.ai.network.layer.LayerBuilderFactory;

import java.util.List;

public class DefaultLayerGenerator extends AbstractLayerGenerator {

    @Override
    public boolean support(Layer layer) {
        return true;
    }

    @Override
    public void generate(Layer layer, List<Layer> layerFroms, ComputationGraphConfiguration.GraphBuilder graphBuilder) {
        Builder builder = LayerBuilderFactory.create(layer);
        graphBuilder.layer(layer.getId(), builder.build(), parseLayerIds(layerFroms));
    }

}
