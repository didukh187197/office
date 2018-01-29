package com.company.office.web.requeststepcommunication;

import com.company.office.common.OfficeTools;
import com.company.office.entity.Request;
import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.RequestStepCommunication;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.gui.ComponentsHelper;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class RequestStepCommunicationEdit extends OfficeEditor<RequestStepCommunication> {

    @Inject
    private OfficeTools officeTools;

    @WindowParam
    private Request request;

    @Named("fieldGroup.initiator")
    private PickerField initiatorField;

    @Named("fieldGroupRecepient.recepient")
    private PickerField recepientField;

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

        if (officeTools.isAdmin())
            return;

        Component closeBtn = getComponentNN("closeBtn");
        closeBtn.setVisible(false);

        if (PersistenceHelper.isNew(getItem())) {
            initiatorField.setValue(officeTools.getActiveUser());
            disableContainer("answerBox");
        } else {
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
                    recepientField.setEditable(false);
                }
            }
        }
    }

    private void disableContainer(String containerID) {
        Container container = (Container) getComponentNN(containerID);
        boolean enabled = false;
        ComponentsHelper.walkComponents(container, (component, name) -> {
            if (component instanceof FieldGroup) {
                ((FieldGroup) component).setEditable(enabled);
            } else if (component instanceof Table) {
                ((Table) component).getActions().forEach(action -> action.setEnabled(enabled));
            } else if (!(component instanceof Container)) {
                component.setEnabled(enabled);
            }
        });
        getComponentNN("btnShowQuestionFile").setEnabled(true);
        getComponentNN("btnShowAnswerFile").setEnabled(true);
    }

    public void onBtnShowQuestionFileClick() {
    }

    public void onBtnShowAnswerFileClick() {
    }

    public void onReadBtnClick() {
    }
}