package com.company.office.web.requestcommunication;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.RequestCommunication;

import java.util.Map;

public class RequestCommunicationEdit extends AbstractEditor<RequestCommunication> {

    @Override
    public void init(Map<String, Object> params) {
        getDialogOptions().setWidth(getComponentNN("fieldGroup").getWidth());
    }

}