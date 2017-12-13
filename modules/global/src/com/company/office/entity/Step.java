package com.company.office.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import com.haulmont.chile.core.annotations.NumberFormat;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.security.entity.User;

@NamePattern("%s (%s) |description,position")
@Table(name = "OFFICE_STEP")
@Entity(name = "office$Step")
public class Step extends StandardEntity {
    private static final long serialVersionUID = -762436337912584846L;

    @Column(name = "IDENTFIER")
    protected Integer identfier;

    @NotNull
    @Column(name = "POSITION_", nullable = false)
    protected Integer position;

    @OnDelete(DeletePolicy.UNLINK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    protected User user;



    @Column(name = "DESCRIPTION", length = 100)
    protected String description;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "step")
    protected List<StepAction> actions;

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