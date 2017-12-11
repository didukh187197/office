package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Column;
import com.haulmont.cuba.security.entity.User;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Extends;

@Extends(User.class)
@Entity(name = "office$ExtUser")
public class ExtUser extends User {
    private static final long serialVersionUID = -8692350638050790495L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    protected Request request;

    @Column(name = "REQUEST_POSITION")
    protected Integer requestPosition;

    @Column(name = "REQUEST_COUNT")
    protected Integer requestCount;

    @Column(name = "REQUEST_THRESHOLD")
    protected Integer requestThreshold;




    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }


    public void setRequestPosition(Position requestPosition) {
        this.requestPosition = requestPosition == null ? null : requestPosition.getId();
    }

    public Position getRequestPosition() {
        return requestPosition == null ? null : Position.fromId(requestPosition);
    }



    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    public void setRequestThreshold(Integer requestThreshold) {
        this.requestThreshold = requestThreshold;
    }

    public Integer getRequestThreshold() {
        return requestThreshold;
    }



}