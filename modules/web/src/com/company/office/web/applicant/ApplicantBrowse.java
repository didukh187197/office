package com.company.office.web.applicant;

import com.company.office.entity.Applicant;
import com.company.office.entity.Request;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;
import com.haulmont.cuba.gui.components.Component;

public class ApplicantBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<Applicant, UUID> applicantsDs;

    @Named("applicantsTable.request")
    private Action requestAction;

    @Override
    public void init(Map<String, Object> params) {
        requestAction.setEnabled(false);
        applicantsDs.addItemChangeListener(e ->
            requestAction.setEnabled(e.getItem() != null)
        );
    }

    public void onRequest(Component source) {
        Request request = applicantsDs.getItem().getRequest();

        if (request == null) {
            request = new Request();
            request.setApplicant(applicantsDs.getItem());
        }

        openEditor("office$Request.edit", request, WindowManager.OpenType.THIS_TAB);
    }
}