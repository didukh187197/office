package com.company.office.web.screens;

import com.company.office.entity.Position;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class PositionsScreen extends AbstractWindow {

    @Inject
    private CollectionDatasource<Position, UUID> positionsDs;

    @WindowParam
    private Position selectedPosition;

    @Override
    public void init(Map<String, Object> params) {
        if (selectedPosition != null) {
            positionsDs.refresh();
            ((Table) getComponentNN("table")).setSelected(selectedPosition);
        }
    }

    public void onOkBtnClick() {
        this.close(Window.COMMIT_ACTION_ID);
    }

    public void onCloseBtnClick() {
        this.close(Window.CLOSE_ACTION_ID);
    }
}