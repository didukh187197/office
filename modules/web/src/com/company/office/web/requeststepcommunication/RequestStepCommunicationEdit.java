package com.company.office.web.requeststepcommunication;

import com.company.office.OfficeConfig;
import com.company.office.common.OfficeCommon;
import com.company.office.common.OfficeTools;
import com.company.office.entity.Request;
import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.RequestStepCommunication;
import com.company.office.web.officeweb.OfficeWeb;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.export.ExportDisplay;
import com.haulmont.cuba.gui.export.ExportFormat;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RequestStepCommunicationEdit extends OfficeEditor<RequestStepCommunication> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private OfficeCommon officeCommon;

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

    @Named("fieldGroup.closed")
    private DateField closedField;

    @Named("fieldGroupAnswer.recepient")
    private PickerField recepientField;
    
    @Named("fieldGroupAnswer.answer")
    private ResizableTextArea answerField;

    @Inject
    private LookupField recepientLookup;

    @Inject
    private Messages messages;

    private boolean closeFromExtraActions = false;
    private boolean newItem = false;

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
    protected void initNewItem(Entity item) {
        newItem = true;
        ((RequestStepCommunication) item).setInitiator(officeTools.getActiveUser());
    }

    @Override
    protected void postInit() {
        super.postInit();

        User activeUser = officeTools.getActiveUser();
        Component closeBtn = getComponentNN("closeBtn");
        closeBtn.setVisible(false);

        if (PersistenceHelper.isNew(getItem())) {
            disableContainer("answerBox");
            makeRecepientLookupOptions();
        } else {
            getComponentNN("recepientBox").setVisible(false);
            recepientField.setVisible(true);

            if (officeTools.isAdmin()) {
                initiatorField.setEditable(true);
                recepientField.setEditable(true);
                closedField.setEditable(true);
            } else {
                if (getItem().getClosed() != null) {
                    disableContainer("tabSheet");
                    getComponentNN("okBtn").setEnabled(false);
                } else if (activeUser.equals(getItem().getInitiator())) {
                    if (getItem().getAnswer() != null) {
                        closeBtn.setVisible(true);
                        disableContainer("tabSheet");
                    } else {
                        disableContainer("answerBox");
                    }
                } else if (activeUser.equals(getItem().getRecepient())) {
                    disableContainer("questionBox");
                    answerField.setRequired(true);
                } else {
                    disableContainer("tabSheet");
                    getComponentNN("okBtn").setEnabled(false);
                }
            }
        }
        setButtonParams();
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        super.postCommit(committed, close);

        if (!closeFromExtraActions) {
            String itemAction = newItem ? messages.getMainMessage("logs.created") : messages.getMainMessage("logs.edited");
            request.getLogs().add(
                    officeCommon.newLogItem(request, getLogRecepient(), makeName() + itemAction, getItem())
            );
        }
        return true;
    }

    private void makeRecepientLookupOptions() {
        List<User> list = new ArrayList<>();
        switch (officeTools.getActiveGroupType()) {
            case Managers:
                list.add(request.getApplicant());
                list.add(getItem().getRequestStep().getUser());
                break;
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
        officeWeb.showFile(getItem().getQuestionFile());
    }

    public void onBtnShowAnswerFileClick() {
        officeWeb.showFile(getItem().getAnswerFile());
    }

    private String makeName() {
        return String.format( getMessage("communication.name") + " ", officeTools.left(getItem().getQuestion(),20) );
    }

    public void onCloseBtnClick() {
        showOptionDialog(
                "",
                getMessage("dialog.close"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            closedField.setValue(new Date());
                            request.getLogs().add(
                                    officeCommon.newLogItem(request, getLogRecepient(), makeName() + messages.getMainMessage("logs.closed"), getItem())
                            );
                            closeFromExtraActions = true;
                            commitAndClose();
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
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

    private User getLogRecepient() {
        return getItem().getInitiator().equals(officeTools.getActiveUser()) ? getItem().getRecepient() : getItem().getInitiator();
    }

}