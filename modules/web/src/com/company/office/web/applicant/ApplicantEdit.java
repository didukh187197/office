package com.company.office.web.applicant;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.gui.components.*;
import com.company.office.entity.Applicant;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class ApplicantEdit extends AbstractEditor<Applicant> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Named("fieldGroup.request")
    private PickerField requestField;

    @Override
    public void init(Map<String, Object> params) {
        if (officeConfig.getApplicantsGroupQuery() != null) {
            usersDs.setQuery(officeConfig.getApplicantsGroupQuery());
        }
    }

    @Override
    protected void initNewItem(Applicant item) {
        requestField.setVisible(false);
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        if (super.postCommit(committed, close)) {
            requestField.setVisible(true);
        }
        return super.postCommit(committed, close);
    }
}