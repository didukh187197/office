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

@NamePattern("%s (%s) |description,position")
@Table(name = "OFFICE_STEP")
@Entity(name = "office$Step")
public class Step extends BaseUuidEntity implements Creatable {
    private static final long serialVersionUID = -762436337912584846L;

    @NumberFormat(pattern = "#")
    @Column(name = "IDENTFIER")
    protected Integer identfier;

    @NotNull
    @Column(name = "POSITION_", nullable = false)
    protected Integer position;

    @OnDeleteInverse(DeletePolicy.DENY)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected User user;



    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @Composition
    @OneToMany(mappedBy = "step")
    protected List<StepAction> actions;

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


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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




    public void setPosition(Position position) {
        this.position = position == null ? null : position.getId();
    }

    public Position getPosition() {
        return position == null ? null : Position.fromId(position);
    }


}