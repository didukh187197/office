package com.company.office.listener;

import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.cuba.core.EntityManager;
import com.company.office.entity.Request;

@Component("office_RequestEntityListener")
public class RequestEntityListener implements BeforeDeleteEntityListener<Request> {


    @Override
    public void onBeforeDelete(Request entity, EntityManager entityManager) {

    }


}