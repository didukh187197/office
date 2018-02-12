package com.company.office.broadcast;

import org.springframework.context.ApplicationEvent;

public class LogsCreatedEvent extends ApplicationEvent {

    public LogsCreatedEvent(String info) {
        super(info);
    }

    public String getInfo() {
        return (String) getSource();
    }
}
