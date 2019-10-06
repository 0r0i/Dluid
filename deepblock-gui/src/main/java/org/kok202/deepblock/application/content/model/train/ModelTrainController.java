package org.kok202.deepblock.application.content.model.train;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import lombok.Data;

@Data
public class ModelTrainController extends AbstractModelTrainController {
    @FXML private Label labelTrainingProgress;
    @FXML private TextArea textAreaTrainingLog;
    @FXML private ProgressBar progressBarTrainingProgress;
    @FXML private Button buttonTrainingOneTime;
    @FXML private Button buttonTrainingNTime;
    @FXML private Button buttonTrainingStop;

    public AnchorPane createView() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/frame/content/model/train/training.fxml"));
        fxmlLoader.setController(this);
        AnchorPane content = fxmlLoader.load();
        return content;
    }

    @Override
    protected void initialize() throws Exception {
        buttonTrainingOneTime.setDisable(false);
        buttonTrainingNTime.setDisable(false);
        buttonTrainingStop.setDisable(true);
        buttonTrainingOneTime.setOnAction(event -> buttonTrainingOneTimeActionHandler());
        buttonTrainingNTime.setOnAction(event -> buttonTrainingNTimeActionHandler());
    }

    private void buttonTrainingOneTimeActionHandler(){
        TrainTask trainTask = new TrainTask();
        trainTask.bindWithComponent(this);
        Thread thread = new Thread(trainTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void buttonTrainingNTimeActionHandler(){
        TrainTask trainTask = new TrainTask();
        trainTask.bindWithComponent(this);
        Thread thread = new Thread(trainTask);
        thread.setDaemon(true);
        thread.start();
    }
}
