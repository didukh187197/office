package com.company.office.web.request;

import com.company.office.entity.*;
import com.company.office.service.ToolsService;
import com.company.office.web.officeweb.OfficeWeb;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class RequestBrowse extends AbstractLookup {

    @Inject
    private ToolsService toolsService;

    @Inject
    private OfficeWeb officeWeb;

    @Inject
    private GroupDatasource<Request, UUID> requestsDs;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private GroupTable<Request> table;

    @Inject
    private TabSheet tabSheet;

    @Override
    public void init(Map<String, Object> params) {
        addListeners();
        setUserInterface();
    }

    private void addListeners() {
        CreateAction createAction = (CreateAction) table.getActionNN("create");
        createAction.setAfterCommitHandler(e -> {
            requestsDs.refresh();

            Request request = requestsDs.getItem();
            if (request.getStep() != null) {
                if (request.getStep().getUser() != null) {
                    officeWeb.showWarningMessage(this, getMessage("request.assignedTo") + request.getStep().getUser().getName());
                } else {
                    officeWeb.showWarningMessage(this, getMessage("request.noAvailableWorkers") + request.getStep().getPosition().getDescription());
                }
            }
        });

        EditAction editAction = (EditAction) table.getActionNN("edit");
        editAction.setBeforeActionPerformedHandler(() -> {
            switch (toolsService.getActiveGroupType()) {
                case Workers:
                    if (requestsDs.getItem().getStep().getApproved() != null) {
                        officeWeb.showWarningMessage(this, getMessage("edit.alreadyApproved"));
                        return false;
                    }
                    break;

                case Applicants:
                    if (requestsDs.getItem().getStep().getSubmitted() != null) {
                        officeWeb.showWarningMessage(this, getMessage("edit.alreadySubmitted"));
                        return false;
                    }
                    if (requestsDs.getItem().getStep().getApproved() != null) {
                        officeWeb.showWarningMessage(this, getMessage("edit.alreadyApproved"));
                        return false;
                    }
                    break;
            }

            return true;
        });

        editAction.setAfterCommitHandler(e -> {
            requestsDs.refresh();
            //getDsContext().get("stepsDs").refresh();
        });

        PopupButton extraActionsBtn = (PopupButton) getComponentNN("extraActionsBtn");
        requestsDs.addItemChangeListener(e -> {
            extraActionsBtn.setEnabled(false);

            if ((e.getItem() == null) || (e.getItem().getStep() == null) || (e.getItem().getStep().getState() == null))
                return;

            if (!table.getSelected().isEmpty()) {
                extraActionsBtn.setEnabled(true);
                extraActionsBtn.getAction("findUser").setEnabled(e.getItem().getStep().getState().equals(State.Waiting));
            }
            focusOnStep();
        });

        tabSheet.addSelectedTabChangeListener(e -> {
            if (e.getSelectedTab().getName().equals("stepsTab")) {
                focusOnStep();
            }
        });
    }

    private void focusOnStep() {
        if (requestsDs.getItem() == null)
            return;

        Table stepsTable = (Table) getComponentNN("stepsTable");
        if ((requestsDs.getItem().getStep() != null) && (!stepsTable.getDatasource().getItems().isEmpty())) {
            stepsTable.setSelected(requestsDs.getItem().getStep());
        }
    }

    private void setUserInterface() {
        if (toolsService.isAdmin())
            return;

        switch (toolsService.getActiveGroupType()) {
            case Registrators:
                getComponentNN("extraActionsBtn").setVisible(false);
                table.getActionNN("remove").setVisible(false);
                break;

            case Managers:
                getComponentNN("extraActionsBtn").setVisible(true);
                table.getActionNN("create").setVisible(false);
                table.getActionNN("edit").setVisible(false);
                table.getActionNN("edit").setEnabled(false);
                table.getActionNN("remove").setVisible(false);
                break;

            case Workers:
                requestsDs.setQuery(String.format("select e from office$Request e where e.step.user.id = '%s' order by e.moment", toolsService.getActiveUser().getId()));
                getComponentNN("extraActionsBtn").setVisible(false);
                table.getActionNN("create").setVisible(false);
                table.getActionNN("remove").setVisible(false);
                tabSheet.setSelectedTab("stepsTab");
                break;

            case Applicants:
                requestsDs.setQuery(String.format("select e from office$Request e where e.applicant.id = '%s' order by e.moment", toolsService.getActiveUser().getId()));
                getComponentNN("extraActionsBtn").setVisible(false);
                table.getActionNN("create").setVisible(false);
                table.getActionNN("remove").setVisible(false);
                tabSheet.setSelectedTab("stepsTab");
                break;
        }
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

    public Component performedGenerator(RequestStepAction requestStepAction) {
        CheckBox checkBox = componentsFactory.createComponent(CheckBox.class);

        checkBox.setValue(false);

        if (requestStepAction.getType() == ActionType.sendFile) {
            if (requestStepAction.getFile() != null) {
                checkBox.setValue(true);
            }
        } else
        if (requestStepAction.getType() == ActionType.sendMessage) {
            if (requestStepAction.getMessage() != null) {
                checkBox.setValue(true);
            }
        }
        return checkBox;
    }

    /*
    Browse actions methods
     */
    public void onFindUser(Component source) {
    }

    public void onChangeStep(Component source) {
    }

    public void onSuspend(Component source) {
    }

    public void onResume(Component source) {
    }

    public void onCancel(Component source) {
    }

    private void showStep(Position position) {
        if (position == null) {
            officeWeb.showWarningMessage(this, "null");
        } else {
            officeWeb.showWarningMessage(this, position.getDescription());
        }
    }

    public void onArchive(Component source) {
        openLookup("positions-screen.xml",
                items -> {
                    if (!items.isEmpty()) {
                        for (Object item : items) {
                            showStep(((Position) item));
                        }
                    }
                },
                WindowManager.OpenType.DIALOG,
                ParamsMap.of("selectedStep", requestsDs.getItem().getStep().getPosition())
        );
    }

}