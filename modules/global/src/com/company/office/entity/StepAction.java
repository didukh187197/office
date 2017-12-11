package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Table(name = "OFFICE_STEP_ACTION")
@Entity(name = "office$StepAction")
public class StepAction extends StandardEntity {
    private static final long serialVersionUID = 8530702335739023159L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STEP_ID")
    protected Step step;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected String type;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @NotNull
    @Column(name = "WORK_DAYS", nullable = false)
    protected Integer workDays;


    @Column(name = "RESULT_REQUIRED")
    protected Boolean resultRequired;

    public void setStep(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
    }


    public void setResultRequired(Boolean resultRequired) {
        this.resultRequired = resultRequired;
    }

    public Boolean getResultRequired() {
        return resultRequired;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setType(ActionType type) {
        this.type = type == null ? null : type.getId();
    }

    public ActionType getType() {
        return type == null ? null : ActionType.fromId(type);
    }

    public void setWorkDays(Integer workDays) {
        this.workDays = workDays;
    }

    public Integer getWorkDays() {
        return workDays;
    }


}