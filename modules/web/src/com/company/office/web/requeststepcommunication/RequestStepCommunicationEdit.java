package com.company.office.web.requeststepcommunication;

import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.RequestStepCommunication;

import java.util.Map;

public class RequestStepCommunicationEdit extends OfficeEditor<RequestStepCommunication> {

    @Override
    public void init(Map<String, Object> params) {
        super.additional();
    }

}