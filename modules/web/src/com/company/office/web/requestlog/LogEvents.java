package com.company.office.web.requestlog;

import com.company.office.common.OfficeCommon;
import com.company.office.common.OfficeTools;
import com.company.office.entity.GroupType;
import com.company.office.entity.RequestLog;
import com.haulmont.cuba.gui.components.*;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.vaadin.server.FontAwesome;

public class LogEvents extends AbstractWindow {

    @Inject
    private OfficeTools officeTools;

    @Inject
    private OfficeCommon officeCommon;

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

    @Override
    public void init(Map<String, Object> params) {
        inboxTable.getColumn("attach").setCaption("");
        inboxTable.setStyleProvider((logItem, property) -> {
            if (property == null) {
                if (logItem.getRead() == null) {
                    return "table-row-marked";
                }
            }
            return null;
        });

        outboxTable.getColumn("attach").setCaption("");

        if (officeTools.getActiveGroupType().equals(GroupType.Applicants)) {
            inboxTable.removeColumn(inboxTable.getColumn("request"));
            outboxTable.removeColumn(outboxTable.getColumn("request"));
        }

        inboxDs.addItemChangeListener(e -> {
            boolean isRead = e.getItem().getRead() != null;
            readAction.setVisible(!isRead);
            unreadAction.setVisible(isRead);
        });
    }

    @Override
    public void ready() {
        checkUnread();
    }

    public Component generateAttachCell(RequestLog entity) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setHtmlEnabled(true);
        label.setValue(entity.getAttachType() != null ? FontAwesome.EXTERNAL_LINK.getHtml() : "");
		return label;
    }

    private void checkUnread() {
        tabSheet.getTab("inboxTab").setCaption(getMessage("logEvents.inbox") + officeCommon.getUnreadLogsInfo());
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
}