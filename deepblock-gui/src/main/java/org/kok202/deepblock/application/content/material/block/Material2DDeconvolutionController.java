package org.kok202.deepblock.application.content.material.block;

import org.kok202.deepblock.ai.entity.enumerator.LayerType;

public class Material2DDeconvolutionController extends AbstractMaterialController {
    @Override
    protected void initialize() throws Exception{
        this.setLayerType(LayerType.DECONVOLUTION_2D_LAYER);
        super.setStyleByBlockType(layerType);
    }
}
