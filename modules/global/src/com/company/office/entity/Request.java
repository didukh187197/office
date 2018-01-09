package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.security.entity.User;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.cuba.core.entity.Updatable;

@NamePattern("%s, %s-%s|applicant,series,number")
@Table(name = "OFFICE_REQUEST")
@Entity(name = "office$Request")
public class Request extends BaseUuidEntity implements Creatable, Updatable {
    private static final long serialVersionUID = 1078634413564627380L;

    @JoinColumn(name = "APPLICANT_ID", unique = true)
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OneToOne(fetch = FetchType.LAZY)
    protected User applicant;

    @Column(name = "APPLICANT_CODE", unique = true, length = 15)
    protected String applicantCode;

    @Column(name = "APPLICANT_PHONE", length = 100)
    protected String applicantPhone;

    @Column(name = "SERIES", length = 10)
    protected String series;

    @NumberFormat(pattern = "#")
    @Column(name = "NUMBER_")
    protected Integer number;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STEP_ID")
    protected RequestStep step;

    @Composition
    @OneToMany(mappedBy = "request")
    protected List<RequestStep> steps;

    @Composition
    @OnDeleteInverse(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "request")
    protected List<RequestLog> logs;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public RequestStep getStep() {
        return step;
    }

    public void setStep(RequestStep step) {
        this.step = step;
    }


    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setLogs(List<RequestLog> logs) {
        this.logs = logs;
    }

    public List<RequestLog> getLogs() {
        return logs;
    }

    public void setSteps(List<RequestStep> steps) {
        this.steps = steps;
    }

    public List<RequestStep> getSteps() {
        return steps;
    }

    public void setApplicantCode(String applicantCode) {
        this.applicantCode = applicantCode;
    }

    public String getApplicantCode() {
        return applicantCode;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}