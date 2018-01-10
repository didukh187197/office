package com.company.office.web.officeeditor;
import com.company.office.service.ToolsService;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.TabSheet;

import javax.inject.Inject;

public class OfficeEditor<T extends Entity> extends AbstractEditor {

    @Inject
    private ToolsService toolsService;

    private void checkSystemTab() {
        if (!toolsService.isAdmin()) {
            ((TabSheet) getComponentNN("tabSheet")).getTab("tabSystem").setVisible(false);
        }
    }

    private void setDialogWidth(String componentName) {
        getDialogOptions().setWidth(getComponentNN(componentName).getWidth());
    }

    protected void additional() {
        checkSystemTab();
        setDialogWidth("fieldGroup");
    }

    @Override
    public T getItem() {
        return (T) super.getItem();
    }

}
