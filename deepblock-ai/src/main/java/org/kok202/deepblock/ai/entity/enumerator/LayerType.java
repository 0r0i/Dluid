package org.kok202.deepblock.ai.entity.enumerator;

public enum LayerType {
    NULL,
    DENSE_LAYER,
    CONVOLUTION_1D_LAYER,
    CONVOLUTION_2D_LAYER,
    DECONVOLUTION_2D_LAYER,
    BASE_RECURRENT_LAYER,
    LSTM,

    LOSS_LAYER,
    CNN_LOSS_LAYER,
    RNN_LOSS_LAYER,

    OUTPUT_LAYER,
    DROPOUT_LAYER,
    BATCH_NORMALIZATION,

    /**********************************************************
     * NOT REAL LAYER
     **********************************************************/
    INPUT_LAYER,

    /**********************************************************
     * NOT SUPPORT
     **********************************************************/
    BASE_PRETAIN_NETWORK,
    PRELU_LAYER,

    ELEMENT_WISE_MULTIPLICATION_LAYER,
    EMBEDDING_LAYER,
    EMBEDDING_SEQUENCE_LAYER,
    REPEAT_VECTOR;
}