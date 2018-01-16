package com.company.office.service;

import com.company.office.entity.Request;
import com.haulmont.cuba.security.entity.User;

public interface RequestService {
    String NAME = "office_RequestService";

    Request nextPosition(Request request);
    Request setWorker(Request request);
    Request addLogItem(Request request, User recepient, String info);
}