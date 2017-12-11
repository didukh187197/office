package com.company.office.web.step;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.Step;
import com.haulmont.cuba.gui.components.TextField;

import javax.inject.Named;
import java.util.Map;

public class StepEdit extends AbstractEditor<Step> {

    @Named("fieldGroup.identfier")
    private TextField identfierField;

    @Override
    public void init(Map<String, Object> params) {
        

        identfierField.addValueChangeListener(e -> {

        });
    }
}