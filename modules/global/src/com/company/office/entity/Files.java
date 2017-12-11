package com.company.office.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum Files implements EnumClass<String> {

    file1("f1"),
    file2("f2"),
    file3("f3"),
    file4("f4"),
    file5("f5");

    private String id;

    Files(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Files fromId(String id) {
        for (Files at : Files.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}