package com.company.office.service;


import com.company.office.entity.Request;

public interface RequestService {
    String NAME = "office_RequestService";

    void newRequestStep(Request request);
}