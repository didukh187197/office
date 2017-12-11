package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "OFFICE_REQUEST_ACTION")
@Entity(name = "office$RequestAction")
public class RequestAction extends StandardEntity {
    private static final long serialVersionUID = 1692244204168907981L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    protected Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACT_ID")
    protected StepAction act;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED")
    protected Date created;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEADLINE")
    protected Date deadline;

    @Temporal(TemporalType.DATE)
    @Column(name = "CLOSED")
    protected Date closed;

    @Column(name = "RESULT_")
    protected String result;

    public StepAction getAct() {
        return act;
    }

    public void setAct(StepAction act) {
        this.act = act;
    }



    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }


    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    public Date getClosed() {
        return closed;
    }


    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDeadline() {
        return deadline;
    }


    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }


}