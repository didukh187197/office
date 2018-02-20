package com.company.office.listener;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.core.app.EmailService;
import com.haulmont.cuba.core.global.EmailInfo;
import com.haulmont.cuba.core.global.Messages;
import org.springframework.stereotype.Component;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import java.sql.Connection;
import java.util.Collections;

import com.company.office.entity.RequestLog;

import javax.inject.Inject;

@Component("office_LogListener")
public class LogListener implements AfterInsertEntityListener<RequestLog> {

    @Inject
    private OfficeConfig officeConfig;

    @Inject
    private EmailService emailService;

    @Inject
    private Messages messages;

    private final String MSG_PACK = "com.company.office.listener";

    @Override
    public void onAfterInsert(RequestLog log, Connection connection) {
        if (!officeConfig.getEmailLogs())
            return;

        String recepientEmail = log.getRecepient().getEmail();
        if (recepientEmail != null) {
            EmailInfo emailInfo = new EmailInfo(
                    recepientEmail, // recipients
                    messages.getMessage(MSG_PACK, "request") + " " + log.getRequest().getInstanceName(), // subject
                    log.getSender().getEmail(), // if null - the "from" address will be taken from the "cuba.email.fromAddress" app property
                    "com/company/office/templates/log_item.txt", // body template
                    Collections.singletonMap("logItem", log) // template parameters
            );
            emailService.sendEmailAsync(emailInfo);
        }
    }


}