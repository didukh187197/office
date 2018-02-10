package com.company.office.broadcast;

import com.company.office.entity.RequestLog;
import org.springframework.context.ApplicationEvent;

public class LogCreateEvent extends ApplicationEvent {

    public LogCreateEvent(RequestLog log) {
        super(log);
    }

    public RequestLog getLog() {
        return (RequestLog) getSource();
    }
}
