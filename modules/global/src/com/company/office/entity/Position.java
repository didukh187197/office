package com.company.office.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum Position implements EnumClass<Integer> {

    queueLine1(10),
    line1(20),
    queueLine2(30),
    line2(40),
    queueLine3(50),
    line3(60);

    private Integer id;

    Position(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static Position fromId(Integer id) {
        for (Position at : Position.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}