package com.company.office.web.screens;

import com.company.office.entity.Step;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class StepScreen extends AbstractLookup {

    @Inject
    private CollectionDatasource<Step, UUID> stepsDs;

    @WindowParam
    protected Step selectedStep;

    @Override
    public void init(Map<String, Object> params) {
        if (selectedStep != null) {
            stepsDs.refresh();
            ((Table) getComponentNN("table")).setSelected(selectedStep);
        }
    }
}