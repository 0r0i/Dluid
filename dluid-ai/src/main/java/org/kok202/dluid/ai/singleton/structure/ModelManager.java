package org.kok202.dluid.ai.singleton.structure;

import lombok.Getter;
import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.kok202.dluid.ai.AIFacade;
import org.kok202.dluid.ai.entity.Layer;
import org.kok202.dluid.ai.listener.TrainingEpochListener;
import org.kok202.dluid.ai.network.LayerGraphManagerConverter;
import org.kok202.dluid.ai.network.Model;
import org.kok202.dluid.ai.singleton.AISingleton;
import org.kok202.dluid.ai.util.NumericRecordSetUtil;
import org.kok202.dluid.domain.stream.NumericRecordSet;
import org.kok202.dluid.domain.structure.GraphManager;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ModelManager {
    private ModelInformation modelInformation;
    private ModelParameter modelParameter;
    private List<Model> models;
    private TrainingEpochListener trainingEpochListener;

    public ModelManager(){
        modelInformation = new ModelInformation();
        modelParameter = new ModelParameter();
    }

    /*************************************************************************************************
     /* Initialize
     *************************************************************************************************/
    public void initialize(GraphManager<Layer> layerGraphManager) {
        models = LayerGraphManagerConverter.convert(layerGraphManager);
    }

    public void setTrainListener(TrainingEpochListener trainingEpochListener){
        this.trainingEpochListener = trainingEpochListener;
    }

    /*************************************************************************************************
     /* Train
     *************************************************************************************************/
    public void train(){
        // Collect all input data set
        Map<String, DataSetIterator> dataSetIteratorMap = findAllInputLayerIds()
                .parallelStream()
                .collect(Collectors.toMap(
                    inputLayerId -> inputLayerId,
                    inputLayerId -> {
                        NumericRecordSet featureDataSet = AISingleton.getInstance().getTrainDataManager().getDataSetManager(inputLayerId).getManagedFeatureRecordSet().getNumericRecordSet();
                        NumericRecordSet resultDataSet = AISingleton.getInstance().getTrainDataManager().getDataSetManager(inputLayerId).getManagedResultRecordSet().getNumericRecordSet();
                        return new ListDataSetIterator<>(NumericRecordSetUtil.shuffleAndConvertAsDataSet(featureDataSet, resultDataSet).asList(), modelParameter.getBatchSize());
                    }));

        // Train it alternately.
        for(int epoch = 0; epoch < modelParameter.getEpoch(); epoch++){
            // Reset data set iterator.
            dataSetIteratorMap.entrySet()
                    .parallelStream()
                    .forEach(entry -> entry.getValue().reset());

            while(true){
                long trainedDataSetNumber = dataSetIteratorMap.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue().hasNext())
                        .map(entry -> {
                            String inputLayerId = entry.getKey();
                            DataSet dataSet = entry.getValue().next();
                            Model model = findModel(inputLayerId);
                            model.train(dataSet);
                            return true;
                        })
                        .count();

                if(trainedDataSetNumber == 0)
                    break;
            }

            if(trainingEpochListener != null)
                trainingEpochListener.count(() -> models.stream().mapToDouble(Model::score).sum());
        }
    }

    public void train(String inputLayerId, NumericRecordSet featureDataSet, NumericRecordSet resultDataSet){
        DataSetIterator dataSetIterator = new ListDataSetIterator<>(NumericRecordSetUtil.shuffleAndConvertAsDataSet(featureDataSet, resultDataSet).asList(), modelParameter.getBatchSize());
        findModel(inputLayerId).getComputationGraph().fit(dataSetIterator, AISingleton.getInstance().getModelManager().getModelParameter().getEpoch());
    }

    /*************************************************************************************************
     /* Test
     *************************************************************************************************/
    public NumericRecordSet test(String inputLayerId, String targetResultLayerId){
        return findModel(inputLayerId).test(AIFacade.getTestFeatureSet().getNumericRecordSet(), targetResultLayerId);
    }

    public Evaluation test(String inputLayerId, NumericRecordSet featureDataSet, NumericRecordSet resultDataSet){
        DataSetIterator dataSetIterator = new ListDataSetIterator<>(NumericRecordSetUtil.shuffleAndConvertAsDataSet(featureDataSet, resultDataSet).asList());
        return findModel(inputLayerId).getComputationGraph().evaluate(dataSetIterator);
    }

    /*************************************************************************************************
     /* ETC
     *************************************************************************************************/
    private List<String> findAllInputLayerIds() {
        return models.stream()
                .map(Model::getInputLayerId)
                .collect(Collectors.toList());
    }

    private Model findModel(String inputLayerId){
        for (Model model : models){
            if(model.getInputLayerId().equals(inputLayerId))
                return model;
        }
        throw new RuntimeException("Can not find model which start from input layer : " + inputLayerId);
    }
}
