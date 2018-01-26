package com.company.office.web.requeststepcommunication;

import com.company.office.common.OfficeTools;
import com.company.office.entity.Request;
import com.company.office.web.officeeditor.OfficeEditor;
import com.company.office.entity.RequestStepCommunication;
import com.haulmont.cuba.core.global.PersistenceHelper;
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

    @Inject
    private HBoxLayout questionFileBox;

    @Named("fieldGroup.initiator")
    private PickerField initiatorField;

    @Named("fieldGroup.question")
    private ResizableTextArea questionField;

    @Inject
    private FileUploadField uploadQuestionFile;

    @Inject
    private HBoxLayout answerFileBox;

    @Named("fieldGroupAnswer.recepient")
    private PickerField recepientField;

    @Named("fieldGroupAnswer.answer")
    private ResizableTextArea answerField;

    @Inject
    private FileUploadField uploadAnswerFile;

    @Inject
    private Button readBtn;

    @Override
    public void init(Map<String, Object> params) {
        initiatorField.setEditable(false);
        uploadQuestionFile.setUploadButtonCaption("");
        uploadQuestionFile.setClearButtonCaption("");
        uploadAnswerFile.setUploadButtonCaption("");
        uploadAnswerFile.setClearButtonCaption("");
    }

    @Override
    protected void postInit() {
        super.postInit();
        readBtn.setVisible(false);

        if (PersistenceHelper.isNew(getItem())) {
            initiatorField.setValue(officeTools.getActiveUser());
            answerField.setEnabled(false);
            answerFileBox.setEnabled(false);
        } else {
            if (getItem().getInitiator().equals(officeTools.getActiveUser())) {
                readBtn.setVisible(getItem().getAnswer() != null);

            } else {
                recepientField.setEditable(false);
                questionField.setEnabled(false);
                questionFileBox.setEnabled(false);
            }
        }
    }

    public void onBtnShowQuestionFileClick() {
    }

    public void onBtnShowAnswerFileClick() {
    }

    public void onReadBtnClick() {
    }
}