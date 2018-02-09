package com.company.office.web.officeeditor;

import com.company.office.common.OfficeTools;
import com.company.office.web.officeweb.OfficeWeb;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.TabSheet;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class OfficeEditor<T extends Entity> extends AbstractEditor {
    
    @Inject
    private OfficeTools officeTools;

    @Inject
    private OfficeWeb officeWeb;

    protected boolean readOnly = false;

    @Override
    public void init(Map<String, Object> params) {
        if (params.containsKey("readOnly"))
            readOnly = (boolean) params.get("readOnly");
    }

    @Override
    protected void initNewItem(Entity item) {
        Class cl = item.getClass();
        Class[] paramTypes = new Class[] {Long.class};
        try {
            Method method = cl.getMethod("setMoment", paramTypes);
            Object[] args = new Object[] {officeTools.getMoment()};
            method.invoke(item, args);
        } catch (NoSuchMethodException e) {
            showNotification("NoSuchMethodException");
        } catch (IllegalAccessException e) {
            showNotification("IllegalAccessException");
        } catch (InvocationTargetException e) {
            showNotification("InvocationTargetException");
        }
    }

    @Override
    protected void postInit() {
        if (readOnly) {
            officeWeb.showWarningMessage(this, messages.getMainMessage("readonly"));
            getComponentNN("okBtn").setVisible(false);
        }

        checkSystemTab();
        getDialogOptions().setWidth(getComponentNN("fieldGroup").getWidth());
    }

    @Override
    public T getItem() {
        return (T) super.getItem();
    }

    private void checkSystemTab() {
        if (!officeTools.isAdmin()) {
            TabSheet tabSheet = (TabSheet) getComponentNN("tabSheet");
            getDialogOptions().setHeight(getDialogOptions().getHeight() - 30);
            tabSheet.getTab("tabSystem").setVisible(false);
            tabSheet.setTabsVisible(false);
        }
    }

}
