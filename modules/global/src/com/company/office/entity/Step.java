package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import java.util.Date;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Creatable;
import com.haulmont.cuba.core.entity.Updatable;

@NamePattern("%s|description")
@Table(name = "OFFICE_STEP")
@Entity(name = "office$Step")
public class Step extends BaseUuidEntity implements Creatable, Updatable {
    private static final long serialVersionUID = -762436337912584846L;

    @NumberFormat(pattern = "#")
    @Column(name = "IDENTFIER")
    protected Integer identfier;



    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @Composition
    @OneToMany(mappedBy = "step")
    protected List<StepAction> actions;

    @Composition
    @OneToMany(mappedBy = "step")
    protected List<StepUser> users;

    @Column(name = "CREATE_TS")
    protected Date createTs;

    @Column(name = "CREATED_BY", length = 50)
    protected String createdBy;

    @Column(name = "UPDATE_TS")
    protected Date updateTs;

    @Column(name = "UPDATED_BY", length = 50)
    protected String updatedBy;

    public void setUsers(List<StepUser> users) {
        this.users = users;
    }

    public List<StepUser> getUsers() {
        return users;
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


    public void setIdentfier(Integer identfier) {
        this.identfier = identfier;
    }

    public Integer getIdentfier() {
        return identfier;
    }


    public void setActions(List<StepAction> actions) {
        this.actions = actions;
    }

    public List<StepAction> getActions() {
        return actions;
    }





}