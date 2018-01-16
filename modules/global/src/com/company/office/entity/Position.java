package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import com.haulmont.chile.core.annotations.Composition;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.chile.core.annotations.NamePattern;
import java.util.Date;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.cuba.core.entity.Updatable;
import javax.persistence.OrderBy;
import javax.validation.constraints.Min;

@NamePattern("%s|description")
@Table(name = "OFFICE_POSITION")
@Entity(name = "office$Position")
public class Position extends BaseUuidEntity implements Creatable, Updatable {
    private static final long serialVersionUID = -762436337912584846L;

    @NumberFormat(pattern = "#")
    @Column(name = "IDENTIFIER")
    protected Integer identifier;

    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @NumberFormat(pattern = "#")
    @Min(0)
    @Column(name = "DAYS_FOR_SUBMISSION")
    protected Integer daysForSubmission;

    @NumberFormat(pattern = "#")
    @Min(0)
    @Column(name = "DAYS_FOR_APPROVAL")
    protected Integer daysForApproval;

    @OrderBy("moment")
    @Composition
    @OneToMany(mappedBy = "position")
    protected List<PositionAction> actions;

    @OrderBy("moment")
    @Composition
    @OneToMany(mappedBy = "position")
    protected List<PositionUser> users;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public List<PositionUser> getUsers() {
        return users;
    }

    public void setUsers(List<PositionUser> users) {
        this.users = users;
    }


    public List<PositionAction> getActions() {
        return actions;
    }

    public void setActions(List<PositionAction> actions) {
        this.actions = actions;
    }


    public void setDaysForSubmission(Integer daysForSubmission) {
        this.daysForSubmission = daysForSubmission;
    }

    public Integer getDaysForSubmission() {
        return daysForSubmission;
    }

    public void setDaysForApproval(Integer daysForApproval) {
        this.daysForApproval = daysForApproval;
    }

    public Integer getDaysForApproval() {
        return daysForApproval;
    }


    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public Integer getIdentifier() {
        return identifier;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}