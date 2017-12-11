package com.company.office;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.security.entity.Group;

@Source(type = SourceType.DATABASE)
public interface OfficeConfig extends Config {

    @Property("office.companyName")
    String getCompanyName();
    void setCompanyName(String name);

    @Property("office.applicantsGroup")
    Group getApplicantsGroup();
    void  setApplicantsGroup(Group group);

    @Property("office.workersGroup")
    Group getWorkersGroup();
    void  setWorkersGroup(Group group);
}