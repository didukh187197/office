package com.company.office.web.applicant;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.gui.components.*;
import com.company.office.entity.Applicant;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.web.gui.components.WebButton;
import com.haulmont.cuba.web.gui.components.WebHBoxLayout;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class ApplicantEdit extends AbstractEditor<Applicant> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Inject
    private Frame windowActions;

    @Inject
    private ComponentsFactory componentsFactory;

    @Override
    public void init(Map<String, Object> params) {
        usersDs.setQuery(officeConfig.getApplicantsGroupQuery());

        WebButton btnSave = componentsFactory.createComponent(WebButton.class);
        btnSave.setId("btnSave");
        btnSave.setCaption("Save");
        btnSave.setAction(new BaseAction("save").withHandler(e -> {
            showNotification("Bu!!!!");
        }));

        Object[] items = windowActions.getComponents().toArray();
        for (int i = 0; i < items.length; i++) {
            Component component = (Component) items[i];
            if (component.getId() == null) {
                WebHBoxLayout hBoxLayout = (WebHBoxLayout) component;
                hBoxLayout.add(btnSave);
            }
        }
    }
}