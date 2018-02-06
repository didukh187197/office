package com.company.office.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;

public enum GroupType implements EnumClass<String> {

    Registrators("registrators"),
    Managers("managers"),
    Workers("workers"),
    Applicants("applicants"),
    Others("others");

    private String id;

    GroupType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static GroupType fromId(String id) {
        for (GroupType at : GroupType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}