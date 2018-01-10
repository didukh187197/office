package com.company.office.web.screens;

import com.company.office.entity.Position;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class PositionsScreen extends AbstractLookup {

    @Inject
    private CollectionDatasource<Position, UUID> positionsDs;

    @WindowParam
    protected Position selectedStep;

    @Override
    public void init(Map<String, Object> params) {
        if (selectedStep != null) {
            positionsDs.refresh();
            ((Table) getComponentNN("table")).setSelected(selectedStep);
        }
    }
}