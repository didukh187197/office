package com.company.office.web.applicant;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.Applicant;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class ApplicantEdit extends AbstractEditor<Applicant> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Override
    public void init(Map<String, Object> params) {
        usersDs.setQuery(officeConfig.getApplicantsGroupQuery());
    }
}