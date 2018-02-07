package com.company.office.web.position;

import com.company.office.entity.Position;
import com.company.office.entity.RequestStep;
import com.company.office.web.officeweb.OfficeWeb;
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
    private OfficeWeb officeWeb;

    @Inject
    private CollectionDatasource<Position, UUID> positionsDs;

    @WindowParam
    private RequestStep selectedStep;

    @Override
    public void init(Map<String, Object> params) {
        Position selectedPosition = selectedStep.getPosition();
        positionsDs.refresh();
        ((Table) getComponentNN("table")).setSelected(selectedPosition);
    }

    public void onOkBtnClick() {
        officeWeb.showStep(this, selectedStep);
    }

    public void onCloseBtnClick() {
        this.close(Window.CLOSE_ACTION_ID);
    }
}