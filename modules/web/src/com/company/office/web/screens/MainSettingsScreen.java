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
    private LookupField lookupApplicantsGroup;

    @Inject
    private LookupField lookupWorkersGroup;

    @Inject
    private LookupField lookupInitStep;

    @Inject
    private LookupField lookupFinalStep;

    @Inject
    protected Messages messages;

    @Override
    public void init(Map<String, Object> params) {
        if (officeConfig.getCompanyName() != null)
            txtCompanyName.setValue(officeConfig.getCompanyName());

        if (officeConfig.getApplicantsGroup() != null)
            lookupApplicantsGroup.setValue(officeConfig.getApplicantsGroup());

        if (officeConfig.getWorkersGroup() != null)
            lookupWorkersGroup.setValue(officeConfig.getWorkersGroup());

        if (officeConfig.getInitStep() != null)
            lookupInitStep.setValue(officeConfig.getInitStep());
    }

    public void onBtnOkClick() {
        showOptionDialog(
                messages.getMainMessage("dialog.saveAndClose.title"),
                messages.getMainMessage("dialog.saveAndClose.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            if (lookupApplicantsGroup.getValue() == null) {
                                showMessageDialog(
                                        getMessage("settingsDialog.warning.empty.title"),
                                        String.format(getMessage("settingsDialog.warning.empty.msg"), getMessage("settingsDialog.applicantsGroup")),
                                        MessageType.WARNING.modal(true).closeOnClickOutside(true)
                                );
                                return;
                            }

                            if (lookupWorkersGroup.getValue() == null) {
                                showMessageDialog(
                                        getMessage("settingsDialog.warning.empty.title"),
                                        String.format(getMessage("settingsDialog.warning.empty.msg"), getMessage("settingsDialog.workersGroup")),
                                        MessageType.WARNING.modal(true).closeOnClickOutside(true)
                                );
                                return;
                            }

                            if (lookupApplicantsGroup.getValue() == lookupWorkersGroup.getValue()) {
                                showMessageDialog(
                                        getMessage("settingsDialog.warning.equals.title"),
                                        getMessage("settingsDialog.warning.equals.msg"),
                                        MessageType.WARNING.modal(true).closeOnClickOutside(true)
                                );
                                return;
                            }

                            if (lookupInitStep.getValue() == null) {
                                showMessageDialog(
                                        getMessage("settingsDialog.warning.empty.title"),
                                        String.format(getMessage("settingsDialog.warning.empty.msg"), getMessage("settingsDialog.initStep")),
                                        MessageType.WARNING.modal(true).closeOnClickOutside(true)
                                );
                                return;
                            }

                            if (lookupFinalStep.getValue() == null) {
                                showMessageDialog(
                                        getMessage("settingsDialog.warning.empty.title"),
                                        String.format(getMessage("settingsDialog.warning.empty.msg"), getMessage("settingsDialog.finalStep")),
                                        MessageType.WARNING.modal(true).closeOnClickOutside(true)
                                );
                                return;
                            }

                            officeConfig.setCompanyName(txtCompanyName.getValue());

                            officeConfig.setApplicantsGroup(lookupApplicantsGroup.getValue());
                            officeConfig.setApplicantsGroupQuery(
                                    String.format("select e from sec$User e where e.group.id = '%s'", officeConfig.getApplicantsGroup().getId())
                            );

                            officeConfig.setWorkersGroup(lookupWorkersGroup.getValue());
                            officeConfig.setWorkersGroupQuery(
                                    String.format("select e from sec$User e where e.group.id = '%s'", officeConfig.getWorkersGroup().getId())
                            );

                            officeConfig.setInitStep(lookupInitStep.getValue());
                            officeConfig.setFinalStep(lookupFinalStep.getValue());

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