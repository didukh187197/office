package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.OneToOne;
import java.util.Date;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;

@Table(name = "OFFICE_STEP_ACTION")
@Entity(name = "office$StepAction")
public class StepAction extends BaseUuidEntity implements Creatable {
    private static final long serialVersionUID = 8530702335739023159L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STEP_ID")
    protected Step step;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected String type;

    @Column(name = "WORK_DAYS")
    protected Integer workDays;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;


    @OnDeleteInverse(DeletePolicy.DENY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEMPLATE_ID", unique = true)
    protected FileDescriptor template;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

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


    public void setWorkDays(Integer workDays) {
        this.workDays = workDays;
    }

    public Integer getWorkDays() {
        return workDays;
    }


    public void setTemplate(FileDescriptor template) {
        this.template = template;
    }

    public FileDescriptor getTemplate() {
        return template;
    }


    public void setStep(Step step) {
        this.step = step;
    }

    public Step getStep() {
        return step;
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


}