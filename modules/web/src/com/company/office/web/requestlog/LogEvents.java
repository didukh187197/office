package com.company.office.web.requestlog;

import com.company.office.common.OfficeTools;
import com.company.office.entity.*;
import com.company.office.service.ToolsService;
import com.company.office.web.officeweb.OfficeWeb;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.icons.CubaIcon;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;

public class LogEvents extends AbstractWindow {

    @Inject
    private OfficeTools officeTools;

    @Inject
    private OfficeWeb officeWeb;

    @Inject
    private ToolsService toolsService;

    @Inject
    private DataManager dataManager;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private GroupDatasource<RequestLog, UUID> inboxDs;

    @Inject
    private TabSheet tabSheet;

    @Inject
    private GroupTable<RequestLog> inboxTable;

    @Inject
    private GroupTable<RequestLog> outboxTable;

    @Named("inboxTable.read")
    private Action readAction;

    @Named("inboxTable.unread")
    private Action unreadAction;

    private BaseAction markAction, attachAction;
    private final String PREFIX = "com.company.office.entity.";

    @Override
    public void init(Map<String, Object> params) {
        inboxTable.getColumn("mark").setCaption("");
        inboxTable.getColumn("attach").setCaption("");
        inboxTable.setStyleProvider((logItem, property) -> {
            if ((property == null) && (logItem.getRead() == null))
                return "table-row-marked";
            return null;
        });

        outboxTable.getColumn("attach").setCaption("");

        if (officeTools.getActiveGroupType().equals(GroupType.Applicants)) {
            inboxTable.removeColumn(inboxTable.getColumn("request"));
            outboxTable.removeColumn(outboxTable.getColumn("request"));
        }

        inboxDs.addItemChangeListener(e -> {
            if (e.getItem() == null)
                return;

            boolean isRead = e.getItem().getRead() != null;
            readAction.setVisible(!isRead);
            unreadAction.setVisible(isRead);
        });

        markAction = new BaseAction("markAction") {
            @Override
            public void actionPerform(Component component) {
                if (inboxDs.getItem().getRead() == null) {
                    inboxDs.getItem().setRead(officeTools.addDaysToNow(0));
                } else {
                    inboxDs.getItem().setRead(null);
                }
                getDsContext().commit();
                checkUnread();
            }
        };

        attachAction = new BaseAction("attachAction") {
            @Override
            public void actionPerform(Component component) {
                GroupDatasource<RequestLog, UUID> workDs = (GroupDatasource<RequestLog, UUID>) ((Table) component.getParent()).getDatasource();
                RequestLog log = workDs.getItem();
                String attachType = log.getAttachType().replace(PREFIX, "");
                UUID attachId = log.getAttachID();
                showAttach(attachType, attachId);
            }
        };
    }

    @Override
    public void ready() {
        checkUnread();
    }

    private void showAttach(String attachType, UUID attachId) {
        Map<String, Object> params = new HashMap<>();
        params.put("readOnly", true);

        Entity item;
        switch (attachType) {
            case "RequestStep":
                item = dataManager.load(
                        LoadContext.create(RequestStep.class).setId(attachId).setView("requestStep-view")
                );
                break;
                case "RequestStepAction":
                item = dataManager.load(
                        LoadContext.create(RequestStepAction.class).setId(attachId).setView("requestStepAction-view")
                );
                break;
            case "RequestStepCommunication":
                item = dataManager.load(
                        LoadContext.create(RequestStepCommunication.class).setId(attachId).setView("requestStepCommunication-view")
                );
                break;
            default: {
                officeWeb.showErrorMessage(this, getMessage("logEvents.error.wrongAttachType"));
                return;
            }
        }

        if (item != null) {
            openEditor(item, WindowManager.OpenType.DIALOG, params);
        } else {
             officeWeb.showErrorMessage(this, getMessage("logEvents.error.attachNotFound"));
        }
    }

    public Component generateMarkCell(RequestLog log) {
        LinkButton btn = componentsFactory.createComponent(LinkButton.class);
        btn.setCaption("");
        btn.setIconFromSet(log.getRead() != null ? CubaIcon.CHECK_CIRCLE_O : CubaIcon.CHECK_CIRCLE);
        btn.setAction(markAction);
        return btn;
    }

    public Component generateAttachCell(RequestLog log) {
        if (log.getAttachType() != null) {
            if (!log.getAttachType().replace(PREFIX, "").equals("Request")) {
                LinkButton btn = componentsFactory.createComponent(LinkButton.class);
                btn.setCaption("");
                btn.setIconFromSet(CubaIcon.EXTERNAL_LINK);
                btn.setAction(attachAction);
                return btn;
            }
        }
        return null;
    }

    private void checkUnread() {
        long count = toolsService.unreadLogsCount();
        getComponentNN("readAllBtn").setEnabled(count != 0);
        getComponentNN("unreadAllBtn").setEnabled(inboxDs.getItems().size() != count);
        tabSheet.getTab("inboxTab").setCaption(getMessage("logEvents.inbox") + officeTools.unreadLogsInfo(count));
    }

    public void onRead() {
        inboxDs.getItem().setRead(officeTools.addDaysToNow(0));
        getDsContext().commit();
        checkUnread();
    }

    public void onUnread() {
        inboxDs.getItem().setRead(null);
        getDsContext().commit();
        checkUnread();
    }

    public void onReadAllBtnClick() {
        Date date = officeTools.addDaysToNow(0);
        for (RequestLog log : inboxDs.getItems()) {
            log.setRead(date);
        }
        getDsContext().commit();
        checkUnread();
    }

    public void onUnreadAllBtnClick() {
        for (RequestLog log : inboxDs.getItems()) {
            log.setRead(null);
        }
        getDsContext().commit();
        checkUnread();
    }

    public void onEdit(Component component) {
        GroupDatasource<RequestLog, UUID> workDs = (GroupDatasource<RequestLog, UUID>) ((Table) component).getDatasource();
        Map<String, Object> params = new HashMap<>();
        params.put("readOnly", true);
        openEditor(workDs.getItem().getRequest(), WindowManager.OpenType.DIALOG.maximized(true),params);
    }
}