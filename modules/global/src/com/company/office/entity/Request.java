package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.security.entity.User;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@NamePattern("%s %s|series,number")
@Table(name = "OFFICE_REQUEST")
@Entity(name = "office$Request")
public class Request extends StandardEntity {
    private static final long serialVersionUID = 1078634413564627380L;

    @OnDelete(DeletePolicy.UNLINK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICANT_ID")
    protected Applicant applicant;

    @Column(name = "SERIES", length = 10)
    protected String series;

    @Column(name = "NUMBER_")
    protected Integer number;

    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STEP_ID")
    protected Step step;

    @OnDelete(DeletePolicy.UNLINK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected User user;

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED")
    protected Date created;

    @Temporal(TemporalType.DATE)
    @Column(name = "CLOSED")
    protected Date closed;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "request")
    protected List<RequestAction> acts;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "request")
    protected List<RequestStatus> states;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "request")
    protected List<RequestCommunication> communications;


    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Applicant getApplicant() {
        return applicant;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }


    public void setSeries(String series) {
        this.series = series;
    }

    public String getSeries() {
        return series;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }


    public void setStep(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }


    public void setCommunications(List<RequestCommunication> communications) {
        this.communications = communications;
    }

    public List<RequestCommunication> getCommunications() {
        return communications;
    }


    public List<RequestAction> getActs() {
        return acts;
    }

    public void setActs(List<RequestAction> acts) {
        this.acts = acts;
    }








    public void setStates(List<RequestStatus> states) {
        this.states = states;
    }

    public List<RequestStatus> getStates() {
        return states;
    }


    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setClosed(Date closed) {
        this.closed = closed;
    }

    public Date getClosed() {
        return closed;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}