package com.company.office;

import com.company.office.entity.Step;
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
    void setApplicantsGroup(Group group);

    @Property("office.queries.applicantsGroup")
    String getApplicantsGroupQuery();
    void setApplicantsGroupQuery(String query);

    @Property("office.workersGroup")
    Group getWorkersGroup();
    void setWorkersGroup(Group group);

    @Property("office.queries.workersGroup")
    String getWorkersGroupQuery();
    void setWorkersGroupQuery(String query);

    @Property("office.initStep")
    Step getInitStep();
    void setInitStep(Step step);

}