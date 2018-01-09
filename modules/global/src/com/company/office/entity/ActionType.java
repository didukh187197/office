package com.company.office.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;
import javax.annotation.Nullable;

public enum ActionType implements EnumClass<String> {

    sendFile("file"),
    sendMessage("message"),
    makeCall("call"),
    visitOffice("visit"),
    closeRequest("close");

    private String id;

    ActionType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static ActionType fromId(String id) {
        for (ActionType at : ActionType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}