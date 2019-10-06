package org.kok202.deepblock.application.content.component;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.kok202.deepblock.ai.entity.Layer;
import org.kok202.deepblock.application.Util.TextFieldUtil;

public class ComponentFeedforwardParamController extends AbstractLayerComponentController {

    @FXML private Label labelInputSize;
    @FXML private Label labelOutputSize;

    @FXML private TextField textFieldInputSize;
    @FXML private TextField textFieldOutputSize;

    public ComponentFeedforwardParamController(Layer layer) {
        super(layer);
    }

    @Override
    public AnchorPane createView() throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/frame/content/component/feedforward_param.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    @Override
    protected void initialize() throws Exception {
        TextFieldUtil.applyPositiveIntegerFilter(textFieldInputSize, 1);
        TextFieldUtil.applyPositiveIntegerFilter(textFieldOutputSize, 1);
        if(layer.getProperties().getInputSize()[0] > 0) {
            textFieldInputSize.setText(String.valueOf(layer.getProperties().getInputSize()[0]));
        }
        if(layer.getProperties().getOutputSize()[0] > 0){
            textFieldOutputSize.setText(String.valueOf(layer.getProperties().getOutputSize()[0]));
        }
        textFieldInputSize.textProperty().addListener(changeListener -> textFieldChangeHandler());
        textFieldOutputSize.textProperty().addListener(changeListener -> textFieldChangeHandler());
    }

    private void textFieldChangeHandler(){
        changeInputSize();
        changeOutputSize();
        notifyLayerDataChanged();
    }

    private void changeInputSize(){
        int x = TextFieldUtil.parseInteger(textFieldInputSize, 1);
        layer.getProperties().setInputSize(x);
    }

    private void changeOutputSize(){
        int y = TextFieldUtil.parseInteger(textFieldOutputSize, 1);
        layer.getProperties().setOutputSize(y);
    }
}
