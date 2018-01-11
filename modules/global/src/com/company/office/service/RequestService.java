package com.company.office.service;

import com.company.office.entity.Request;

public interface RequestService {
    String NAME = "office_RequestService";

    void nextPosition(Request request);
    boolean setWorker(Request request);
    void addLogItem(Request request, String info);
}