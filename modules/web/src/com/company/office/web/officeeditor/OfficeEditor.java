package com.company.office.web.officeeditor;

import com.company.office.common.OfficeTools;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.TabSheet;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class OfficeEditor<T extends Entity> extends AbstractEditor {
    
    @Inject
    private OfficeTools officeTools;
    
    @Override
    protected void postInit() {
        if (PersistenceHelper.isNew(getItem())) {
            Class cl = getItem().getClass();
            Class[] paramTypes = new Class[] {Long.class};
            try {
                Method method = cl.getMethod("setMoment", paramTypes);
                Object[] args = new Object[] {officeTools.getMoment()};
                method.invoke(getItem(),args);
            } catch (NoSuchMethodException e) {
                showNotification("NoSuchMethodException");
            } catch (IllegalAccessException e) {
                showNotification("IllegalAccessException");
            } catch (InvocationTargetException e) {
                showNotification("InvocationTargetException");
            }
        }
    }

    @Override
    public T getItem() {
        return (T) super.getItem();
    }

    private void checkSystemTab() {
        if (!officeTools.isAdmin()) {
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

}
