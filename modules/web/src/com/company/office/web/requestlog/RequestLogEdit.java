package com.company.office.web.requestlog;

import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.RequestLog;

import java.util.Map;

public class RequestLogEdit extends OfficeEditor<RequestLog> {

    @Override
    public void init(Map<String, Object> params) {
        super.additional();
    }
}