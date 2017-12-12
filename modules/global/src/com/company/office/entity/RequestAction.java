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
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.OneToOne;
import com.haulmont.chile.core.annotations.Composition;

@Table(name = "OFFICE_REQUEST_ACTION")
@Entity(name = "office$RequestAction")
public class RequestAction extends StandardEntity {
    private static final long serialVersionUID = 1692244204168907981L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    protected Request request;

    @Column(name = "TYPE_")
    protected String type;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "DEADLINE")
    protected Date deadline;

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED")
    protected Date created;



    @Temporal(TemporalType.DATE)
    @Column(name = "SUBMITTED")
    protected Date submitted;

    @Temporal(TemporalType.DATE)
    @Column(name = "APPROVED")
    protected Date approved;

    @OnDelete(DeletePolicy.UNLINK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID")
    protected FileDescriptor template;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Column(name = "MESSAGE")
    protected String message;

    public void setApproved(Date approved) {
        this.approved = approved;
    }

    public Date getApproved() {
        return approved;
    }


    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    public Date getSubmitted() {
        return submitted;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setTemplate(FileDescriptor template) {
        this.template = template;
    }

    public FileDescriptor getTemplate() {
        return template;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }


    public void setType(ActionType type) {
        this.type = type == null ? null : type.getId();
    }

    public ActionType getType() {
        return type == null ? null : ActionType.fromId(type);
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