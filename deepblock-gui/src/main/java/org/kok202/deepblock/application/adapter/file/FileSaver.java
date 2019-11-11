package org.kok202.deepblock.application.adapter.file;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import lombok.Data;
import org.kok202.deepblock.application.singleton.AppWidgetSingleton;

import java.io.File;
import java.util.ArrayList;

@Data
public abstract class FileSaver {
    private Button button;
    private Runnable callbackBeforeSave;
    private Runnable callbackAfterSave;
    private ArrayList<FileChooser.ExtensionFilter> extensionFilter;

    public FileSaver(Button button) {
        this.button = button;
        extensionFilter = new ArrayList<>();
    }

    public void initialize(){
        setOnButtonListener();
    }

    private void setOnButtonListener(){
        button.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                runCallbackBeforeSave();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Insert save path");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                fileChooser.getExtensionFilters().addAll(extensionFilter);

                File file = fileChooser.showSaveDialog(AppWidgetSingleton.getInstance().getPrimaryStage());
                if(file != null){
                    saveContent(file);
                    runCallbackAfterSave();
                }
            }
        });
    }

    public void runCallbackBeforeSave() {
        if(callbackBeforeSave == null)
            return;
        callbackBeforeSave.run();
    }

    protected abstract void saveContent(File file);

    public void runCallbackAfterSave() {
        if(callbackAfterSave == null)
            return;
        callbackAfterSave.run();
    }
}
