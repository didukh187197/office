package com.company.office.service;

import com.company.office.entity.Request;

public interface RequestService {
    String NAME = "office_RequestService";

    Request nextPosition(Request request);
    Request setWorker(Request request);
    Request addLogItem(Request request, String info);
}