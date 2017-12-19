package com.company.office.web.requestcommunication;

import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.RequestCommunication;
import com.haulmont.cuba.gui.components.FieldGroup;

import javax.inject.Inject;
import java.util.Map;

public class RequestCommunicationEdit extends AbstractEditor<RequestCommunication> {

    @Inject
    private FieldGroup fieldGroup;

    @Override
    public void init(Map<String, Object> params) {
        getDialogOptions().setWidth(fieldGroup.getWidth()).setHeight("390px");
    }
}