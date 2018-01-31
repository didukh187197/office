package com.company.office.web.requeststepcommunication;

import com.company.office.OfficeConfig;
import com.company.office.common.OfficeTools;
import com.company.office.entity.Request;
import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.RequestStepCommunication;
import com.company.office.web.officeweb.OfficeWeb;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RequestStepCommunicationEdit extends OfficeEditor<RequestStepCommunication> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private OfficeTools officeTools;

    @Inject
    private OfficeWeb officeWeb;

    @Inject
    private DataManager dataManager;

    @WindowParam
    private Request request;

    @Named("fieldGroup.initiator")
    private PickerField initiatorField;

    @Inject
    private LookupField recepientLookup;

    public RequestStepCommunicationEdit() {
    }

    @Override
    public void init(Map<String, Object> params) {
        initiatorField.setEditable(false);

        FileUploadField uploadQuestionFile = (FileUploadField) getComponentNN("uploadQuestionFile");
        FileUploadField uploadAnswerFile = (FileUploadField) getComponentNN("uploadAnswerFile");
        uploadQuestionFile.setUploadButtonCaption("");
        uploadQuestionFile.setClearButtonCaption("");
        uploadAnswerFile.setUploadButtonCaption("");
        uploadAnswerFile.setClearButtonCaption("");
    }

    @Override
    protected void postInit() {
        super.postInit();

        /*
        if (officeTools.isAdmin())
            return;
            */

        Component closeBtn = getComponentNN("closeBtn");
        closeBtn.setVisible(false);

        if (PersistenceHelper.isNew(getItem())) {
            initiatorField.setValue(officeTools.getActiveUser());
            disableContainer("answerBox");
            makeRecepientLookupOptions();
        } else {
            recepientLookup.setEditable(false);
            if (getItem().getClosed() != null) {
                disableContainer("tabSheet");
                getComponentNN("okBtn").setEnabled(false);
            } else {
                if (getItem().getInitiator().equals(officeTools.getActiveUser())) {
                    closeBtn.setVisible(true);
                    if (getItem().getAnswer() != null) {
                        disableContainer("tabSheet");
                    } else {
                        disableContainer("answerBox");
                    }
                } else {
                    disableContainer("questionBox");
                }
            }
        }
        setButtonParams();
    }

    private void makeRecepientLookupOptions() {
        List<User> list = new ArrayList<>();
        switch (officeTools.getActiveGroupType()) {
            case Workers:
                list.add(request.getApplicant());
                list.addAll(getManagers());
                break;
            case Applicants:
                list.add(getItem().getRequestStep().getUser());
                list.addAll(getManagers());
                break;
            default:
                list.add(request.getApplicant());
                list.add(getItem().getRequestStep().getUser());
                list.addAll(getManagers());

        }
        recepientLookup.setOptionsList(list);
    }

    private void disableContainer(String containerID) {
        officeWeb.disableContainer(this, containerID);
    }

    public void onBtnShowQuestionFileClick() {
    }

    public void onBtnShowAnswerFileClick() {
    }

    public void onReadBtnClick() {
    }

    private void setButtonParams() {
        getComponentNN("btnShowQuestionFile").setEnabled(getItem().getQuestionFile() != null);
        getComponentNN("btnShowAnswerFile").setEnabled(getItem().getAnswerFile() != null);
    }

    private List<User> getManagers() {
        LoadContext<User> loadContext = LoadContext.create(User.class)
                .setQuery(LoadContext.createQuery("select e from sec$User e where e.group.id = :grp")
                        .setParameter("grp", officeConfig.getManagersGroup().getId())
                ).setView("_local");

        return dataManager.loadList(loadContext);
    }

}