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
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.OneToOne;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.security.entity.User;
import java.util.UUID;

@Table(name = "OFFICE_REQUEST_STEP_ACTION")
@Entity(name = "office$RequestStepAction")
public class RequestStepAction extends BaseUuidEntity implements Creatable, Updatable {
    private static final long serialVersionUID = 1692244204168907981L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_STEP_ID")
    protected RequestStep requestStep;

    @Column(name = "TYPE_")
    protected String type;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @Lookup(type = LookupType.DROPDOWN, actions = {"lookup", "open", "clear"})
    @OnDeleteInverse(DeletePolicy.DENY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID")
    protected FileDescriptor template;

    @OnDeleteInverse(DeletePolicy.DENY)
    @Composition
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Column(name = "MESSAGE")
    protected String message;

    @Temporal(TemporalType.DATE)
    @Column(name = "SUBMITTED")
    protected Date submitted;

    @Temporal(TemporalType.DATE)
    @Column(name = "APPROVED")
    protected Date approved;

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


    public void setMoment(Long moment) {
        this.moment = moment;
    }

    public Long getMoment() {
        return moment;
    }


    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public void setApproved(Date approved) {
        this.approved = approved;
    }

    public Date getApproved() {
        return approved;
    }


    public void setRequestStep(RequestStep requestStep) {
        this.requestStep = requestStep;
    }

    public RequestStep getRequestStep() {
        return requestStep;
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

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}