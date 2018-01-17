package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.Creatable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.security.entity.User;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.UUID;

@Table(name = "OFFICE_REQUEST_LOG")
@Entity(name = "office$RequestLog")
public class RequestLog extends BaseUuidEntity implements Updatable, Creatable {
    private static final long serialVersionUID = 8281849904778881851L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    protected Request request;

    @Column(name = "INFO")
    protected String info;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID")
    protected User sender;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEPIENT_ID")
    protected User recepient;

    @Column(name = "ATTACH_TYPE", length = 70)
    protected String attachType;

    @Column(name = "ATTACH_ID")
    protected UUID attachID;

    @Temporal(TemporalType.DATE)
    @Column(name = "READ_")
    protected Date read;

    @Column(name = "MOMENT")
    protected Long moment;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;


    public void setAttachType(String attachType) {
        this.attachType = attachType;
    }

    public String getAttachType() {
        return attachType;
    }

    public void setAttachID(UUID attachID) {
        this.attachID = attachID;
    }

    public UUID getAttachID() {
        return attachID;
    }


    public void setMoment(Long moment) {
        this.moment = moment;
    }

    public Long getMoment() {
        return moment;
    }


    public void setRead(Date read) {
        this.read = read;
    }

    public Date getRead() {
        return read;
    }


    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    public void setRecepient(User recepient) {
        this.recepient = recepient;
    }

    public User getRecepient() {
        return recepient;
    }


    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }


    @Override
    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    @Override
    public Date getCreateTs() {
        return createTs;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setUpdateTs(Date updateTs) {
        this.updateTs = updateTs;
    }

    @Override
    public Date getUpdateTs() {
        return updateTs;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }


}