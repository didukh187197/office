package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.entity.ExtUser;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.company.office.entity.Request;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.LookupPickerField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.data.CollectionDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class RequestEdit extends AbstractEditor<Request> {
    @Inject
    private OfficeConfig officeConfig;

    @Named("fieldGroup.series")
    private TextField seriesField;

    @Named("fieldGroup.number")
    private TextField numberField;

    @Named("fieldGroup.applicant")
    private LookupPickerField applicantField;

    @Named("fieldGroup.worker")
    private LookupPickerField workerField;

    @Named("fieldGroup.created")
    private DateField createdField;

    @Inject
    private CollectionDatasource<ExtUser, UUID> applicantsDs;

    @Inject
    private CollectionDatasource<ExtUser, UUID> workersDs;

    @Override
    public void init(Map<String, Object> params) {
        applicantsDs.setQuery(officeConfig.getApplicantsGroupQuery());
        workersDs.setQuery(officeConfig.getWorkersGroupQuery());
    }

    private Boolean isNew = false;

    @Override
    protected void initNewItem(Request item) {
        isNew = true;
    }

    @Override
    protected void postInit() {
        if (isNew) {
            seriesField.setValue(officeConfig.getRequestSeries());
            numberField.setValue(officeConfig.getRequestNumber());
            createdField.setValue(new Date());
        }
        applicantField.requestFocus();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        super.postCommit(committed, close);
        if (isNew) {
            officeConfig.setRequestNumber((Integer) numberField.getValue() + 1);
        }
        return true;
    }
}