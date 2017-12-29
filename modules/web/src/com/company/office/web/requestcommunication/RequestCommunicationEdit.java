package com.company.office.web.requestcommunication;

import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.RequestCommunication;

import java.util.Map;

public class RequestCommunicationEdit extends OfficeEditor<RequestCommunication> {

    @Override
    public void init(Map<String, Object> params) {
        super.additional();
    }

}