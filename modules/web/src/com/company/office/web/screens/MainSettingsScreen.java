package com.company.office.web.screens;

import com.company.office.OfficeConfig;
import com.company.office.entity.Position;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

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
    private LookupField lookupManagersRole;

    @Inject
    private LookupField lookupRegistratorsRole;

    @Inject
    private LookupField lookupApplicantsRole;

    @Inject
    private LookupField lookupWorkersRole;

    @Inject
    private LookupField lookupInitPosition;

    @Inject
    private LookupField lookupFinalPosition;

    @Inject
    private MaskedField applicantPenaltyFiled;

    @Inject
    private MaskedField workerPenaltyFiled;

    @Inject
    private Messages messages;

    @Override
    public void init(Map<String, Object> params) {
        if (officeConfig.getCompanyName() != null)
            txtCompanyName.setValue(officeConfig.getCompanyName());

        // Groups
        initGroupLookup(lookupManagersGroup, officeConfig.getManagersGroup());
        initGroupLookup(lookupRegistratorsGroup, officeConfig.getRegistratorsGroup());
        initGroupLookup(lookupApplicantsGroup, officeConfig.getApplicantsGroup());
        initGroupLookup(lookupWorkersGroup, officeConfig.getWorkersGroup());

        // Roles
        initRoleLookup(lookupManagersRole, officeConfig.getManagersRole());
        initRoleLookup(lookupRegistratorsRole, officeConfig.getRegistratorsRole());
        initRoleLookup(lookupApplicantsRole, officeConfig.getApplicantsRole());
        initRoleLookup(lookupWorkersRole, officeConfig.getWorkersRole());

        // Positions
        initPositionLookup(lookupInitPosition, officeConfig.getInitPosition());
        initPositionLookup(lookupFinalPosition, officeConfig.getFinalPosition());

        //Penalties
        initPenaltyField(applicantPenaltyFiled, officeConfig.getApplicantPenalty());
        initPenaltyField(workerPenaltyFiled, officeConfig.getWorkerPenalty());
    }

    private void initGroupLookup(LookupField lookup, Group value) {
        if (value != null)
            lookup.setValue(value);
    }

    private void initRoleLookup(LookupField lookup, Role value) {
        if (value != null)
            lookup.setValue(value);
    }

    private void initPositionLookup(LookupField lookup, Position value) {
        if (value != null)
            lookup.setValue(value);
    }

    private void initPenaltyField(MaskedField field, Integer value) {
        if (value != null)
            field.setValue(value);
    }

    private void warnEmptyField(String fieldName, String fieldType) {
        String second = fieldType.isEmpty() ? "" : getMessage("settingsDialog.type." + fieldType);
        showNotification(
                String.format(getMessage("settingsDialog.warning.empty.msg"), getMessage("settingsDialog." + fieldName), second),
                NotificationType.ERROR
        );
    }

    public void onBtnOkClick() {
        showOptionDialog(
                messages.getMainMessage("dialog.saveAndClose.title"),
                messages.getMainMessage("dialog.msg"),
                MessageType.CONFIRMATION,
                new Action[] {
                        new DialogAction(DialogAction.Type.YES, Action.Status.NORMAL).withHandler(e -> {
                            if (lookupManagersGroup.getValue() == null) {
                                warnEmptyField("managers", "group");
                                return;
                            }

                            if (lookupRegistratorsGroup.getValue() == null) {
                                warnEmptyField("registrators", "group");
                                return;
                            }

                            if (lookupApplicantsGroup.getValue() == null) {
                                warnEmptyField("applicants", "group");
                                return;
                            }

                            if (lookupWorkersGroup.getValue() == null) {
                                warnEmptyField("workers", "group");
                                return;
                            }

                            //Roles
                            if (lookupManagersRole.getValue() == null) {
                                warnEmptyField("managers", "role");
                                return;
                            }

                            if (lookupRegistratorsRole.getValue() == null) {
                                warnEmptyField("registrators", "role");
                                return;
                            }

                            if (lookupApplicantsRole.getValue() == null) {
                                warnEmptyField("applicants", "role");
                                return;
                            }

                            if (lookupWorkersRole.getValue() == null) {
                                warnEmptyField("workers", "role");
                                return;
                            }

                            if (lookupInitPosition.getValue() == null) {
                                warnEmptyField("initPosition", "");
                                return;
                            }

                            if (lookupFinalPosition.getValue() == null) {
                                warnEmptyField("finalPosition", "");
                                return;
                            }

                            if (applicantPenaltyFiled.getValue() == null) {
                                warnEmptyField("applicantPenalty", "");
                                return;
                            }

                            if (workerPenaltyFiled.getValue() == null) {
                                warnEmptyField("workerPenalty", "");
                                return;
                            }

                            officeConfig.setCompanyName(txtCompanyName.getValue());

                            officeConfig.setManagersGroup(lookupManagersGroup.getValue());
                            officeConfig.setRegistratorsGroup(lookupRegistratorsGroup.getValue());
                            officeConfig.setApplicantsGroup(lookupApplicantsGroup.getValue());
                            officeConfig.setWorkersGroup(lookupWorkersGroup.getValue());

                            officeConfig.setManagersRole(lookupManagersRole.getValue());
                            officeConfig.setRegistratorsRole(lookupRegistratorsRole.getValue());
                            officeConfig.setApplicantsRole(lookupApplicantsRole.getValue());
                            officeConfig.setWorkersRole(lookupWorkersRole.getValue());

                            officeConfig.setInitPosition(lookupInitPosition.getValue());
                            officeConfig.setFinalPosition(lookupFinalPosition.getValue());

                            officeConfig.setApplicantPenalty(Integer.parseInt(applicantPenaltyFiled.getValue()));
                            officeConfig.setWorkerPenalty(Integer.parseInt(workerPenaltyFiled.getValue()));

                            this.close("");
                        }),
                        new DialogAction(DialogAction.Type.NO, Action.Status.PRIMARY)
                }
        );
    }

    public void onBtnCancelClick() {
        this.close("");
    }

    // For testing purposes
    @Inject
    private Button btnProba;

    @Override
    public void ready() {
        btnProba.setVisible(false);
    }

    public void onBtnProbaClick() {}

}