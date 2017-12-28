package com.company.office.web.request;

import com.company.office.OfficeConfig;
import com.company.office.entity.ActionType;
import com.company.office.entity.RequestAction;
import com.haulmont.cuba.gui.components.*;
import com.company.office.entity.Request;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class RequestBrowse extends EntityCombinedScreen {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> applicantDs;

    @Inject
    private CollectionDatasource<User, UUID> workerDs;

    @Inject
    private CollectionDatasource<RequestAction, UUID> actionsDs;

    @Named("fieldsAction.file")
    private FileUploadField fileField;

    @Named("fieldsAction.message")
    private ResizableTextArea messageField;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (officeConfig.getApplicantsGroup() != null) {
            applicantDs.setQuery(officeConfig.getApplicantsGroupQuery());
        }

        if (officeConfig.getWorkersGroupQuery() != null) {
            workerDs.setQuery(officeConfig.getWorkersGroupQuery());
        }

        actionsDs.addItemChangeListener(e -> {
            if (actionsDs.getItem() != null) {
                ActionType actionType = actionsDs.getItem().getType();

                fileField.setVisible(actionType.equals(ActionType.sendFile));
                messageField.setVisible(actionType.equals(ActionType.sendMessage));
            }
        });

    }

    public Component snGenerator(Request request) {
        String res = "";
        if (request.getSeries() != null) {
            res += request.getSeries() + "-";
        }

        if (request.getNumber() != null) {
            res += request.getNumber();
        }

        return new Table.PlainTextCell(res);
    }

}