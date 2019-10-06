package org.kok202.deepblock.application.adapter.file;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.kok202.deepblock.ai.global.AIPropertiesSingleton;
import org.kok202.deepblock.ai.global.structure.ManagedRecordSet;

public class TestFeatureFileFinder extends ExtendedFileFinder {

    public TestFeatureFileFinder(TextField textField, Button button) {
        super(textField, button);
    }

    @Override
    public ManagedRecordSet getManagedRecordSet() {
        return AIPropertiesSingleton.getInstance()
                .getTestProperty()
                .getDataSetManager()
                .getManagedFeatureRecordSet();
    }
}
