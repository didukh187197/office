package com.company.office.web.screens;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import java.util.*;

public class MainSettingsScreen extends AbstractWindow {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private TextField txtCompanyName;

    @Inject
    private LookupField lookupManagersGroup;

    @Inject
    private LookupField lookupRegistratorsGroup;

    @Inject
    private LookupField lookupApplicantsGroup;

    @Inject
    private LookupField lookupWorkersGroup;

    @Inject
    private LookupField lookupInitPosition;

    @Inject
    private LookupField lookupFinalPosition;

    @Inject
    protected Messages messages;

    @Override
    public void init(Map<String, Object> params) {
        if (officeConfig.getCompanyName() != null)
            txtCompanyName.setValue(officeConfig.getCompanyName());

        if (officeConfig.getManagersGroup() != null)
            lookupManagersGroup.setValue(officeConfig.getManagersGroup());

        if (officeConfig.getRegistratorsGroup() != null)
            lookupRegistratorsGroup.setValue(officeConfig.getRegistratorsGroup());

        if (officeConfig.getApplicantsGroup() != null)
            lookupApplicantsGroup.setValue(officeConfig.getApplicantsGroup());

        if (officeConfig.getWorkersGroup() != null)
            lookupWorkersGroup.setValue(officeConfig.getWorkersGroup());

        if (officeConfig.getInitPosition() != null)
            lookupInitPosition.setValue(officeConfig.getInitPosition());

        if (officeConfig.getFinalPosition() != null)
            lookupFinalPosition.setValue(officeConfig.getFinalPosition());
    }

    private void warnEmptyField(String groupName) {
        showNotification(String.format(getMessage("settingsDialog.warning.empty.msg"), getMessage("settingsDialog." + groupName)), NotificationType.ERROR);
        /*
        showMessageDialog(
                getMessage("settingsDialog.warning.empty.title"),
                String.format(getMessage("settingsDialog.warning.empty.msg"), getMessage("settingsDialog." + groupName)),
                MessageType.WARNING.modal(true).closeOnClickOutside(true)
        );
        */
    }

    public void onBtnOkClick() {
        showOptionDialog(
                messages.getMainMessage("dialog.saveAndClose.title"),
                messages.getMainMessage("dialog.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            if (lookupManagersGroup.getValue() == null) {
                                warnEmptyField("managersGroup");
                                return;
                            }

                            if (lookupRegistratorsGroup.getValue() == null) {
                                warnEmptyField("registratorsGroup");
                                return;
                            }

                            if (lookupApplicantsGroup.getValue() == null) {
                                warnEmptyField("applicantsGroup");
                                return;
                            }

                            if (lookupWorkersGroup.getValue() == null) {
                                warnEmptyField("workersGroup");
                                return;
                            }

                            if (lookupInitPosition.getValue() == null) {
                                warnEmptyField("initPosition");
                                return;
                            }

                            if (lookupFinalPosition.getValue() == null) {
                                warnEmptyField("finalPosition");
                                return;
                            }

                            officeConfig.setCompanyName(txtCompanyName.getValue());
                            officeConfig.setManagersGroup(lookupManagersGroup.getValue());
                            officeConfig.setRegistratorsGroup(lookupRegistratorsGroup.getValue());
                            officeConfig.setApplicantsGroup(lookupApplicantsGroup.getValue());
                            officeConfig.setWorkersGroup(lookupWorkersGroup.getValue());
                            officeConfig.setWorkersGroupQuery(
                                    String.format("select e from sec$User e where e.group.id = '%s'", officeConfig.getWorkersGroup().getId())
                            );
                            officeConfig.setApplicantsGroupQuery(
                                    String.format("select e from sec$User e where e.group.id = '%s'", officeConfig.getApplicantsGroup().getId())
                            );
                            officeConfig.setInitPosition(lookupInitPosition.getValue());
                            officeConfig.setFinalPosition(lookupFinalPosition.getValue());

                            this.close("");
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void onBtnCancelClick() {
        this.close("");
    }
}