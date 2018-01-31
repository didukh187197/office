package com.company.office;

import com.company.office.entity.Position;
import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;

@Source(type = SourceType.DATABASE)
public interface OfficeConfig extends Config {

    @Property("office.companyName")
    String getCompanyName();
    void setCompanyName(String name);

    @Property("office.groups.commonGroup")
    Group getCommonGroup();
    void setCommonGroup(Group group);

    @Property("office.groups.managersGroup")
    Group getManagersGroup();
    void setManagersGroup(Group group);

    @Property("office.groups.registratorsGroup")
    Group getRegistratorsGroup();
    void setRegistratorsGroup(Group group);

    @Property("office.groups.applicantsGroup")
    Group getApplicantsGroup();
    void setApplicantsGroup(Group group);

    @Property("office.groups.workersGroup")
    Group getWorkersGroup();
    void setWorkersGroup(Group group);

    // Roles
    @Property("office.roles.managersRole")
    Role getManagersRole();
    void setManagersRole(Role role);

    @Property("office.roles.registratorsRole")
    Role getRegistratorsRole();
    void setRegistratorsRole(Role role);

    @Property("office.roles.applicantsRole")
    Role getApplicantsRole();
    void setApplicantsRole(Role role);

    @Property("office.roles.workersRole")
    Role getWorkersRole();
    void setWorkersRole(Role role);

    @Property("office.positions.initPosition")
    Position getInitPosition();
    void setInitPosition(Position position);

    @Property("office.positions.finalPosition")
    Position getFinalPosition();
    void setFinalPosition(Position position);

}