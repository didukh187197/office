package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.security.entity.User;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Updatable;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NumberFormat;
import javax.persistence.OneToOne;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "OFFICE_REQUEST_STEP")
@Entity(name = "office$RequestStep")
public class RequestStep extends BaseUuidEntity implements Updatable, Creatable {
    private static final long serialVersionUID = 3776301119152373188L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    protected Request request;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID")
    protected Position position;

    @Column(name = "STATE")
    protected String state;

    @OnDeleteInverse(DeletePolicy.DENY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected User user;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "SUBMISSION_TERM")
    protected Date submissionTerm;

    @Temporal(TemporalType.DATE)
    @Column(name = "SUBMITTED")
    protected Date submitted;

    @Temporal(TemporalType.DATE)
    @Column(name = "APPROVAL_TERM")
    protected Date approvalTerm;

    @Temporal(TemporalType.DATE)
    @Column(name = "APPROVED")
    protected Date approved;

    @NumberFormat(pattern = "#")
    @Column(name = "PENALTY")
    protected Integer penalty;

    @OneToMany(mappedBy = "requestStep")
    @Composition
    protected List<RequestStepAction> actions;

    @Composition
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "requestStep")
    protected List<RequestStepCommunication> communications;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;


    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }


    public void setSubmissionTerm(Date submissionTerm) {
        this.submissionTerm = submissionTerm;
    }

    public Date getSubmissionTerm() {
        return submissionTerm;
    }

    public void setApprovalTerm(Date approvalTerm) {
        this.approvalTerm = approvalTerm;
    }

    public Date getApprovalTerm() {
        return approvalTerm;
    }


    public void setCommunications(List<RequestStepCommunication> communications) {
        this.communications = communications;
    }

    public List<RequestStepCommunication> getCommunications() {
        return communications;
    }


    public void setState(State state) {
        this.state = state == null ? null : state.getId();
    }

    public State getState() {
        return state == null ? null : State.fromId(state);
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


    public List<RequestStepAction> getActions() {
        return actions;
    }

    public void setActions(List<RequestStepAction> actions) {
        this.actions = actions;
    }


    public void setPenalty(Integer penalty) {
        this.penalty = penalty;
    }

    public Integer getPenalty() {
        return penalty;
    }


    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
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


}