package com.company.office;

import com.haulmont.cuba.core.config.Config;
import com.haulmont.cuba.core.config.Property;
import com.haulmont.cuba.core.config.Source;
import com.haulmont.cuba.core.config.SourceType;
import com.haulmont.cuba.core.entity.FileDescriptor;
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

    @Property("office.common-files.file1")
    FileDescriptor getFile1();
    void  setFile1(FileDescriptor file);

    @Property("office.common-files.file2")
    FileDescriptor getFile2();
    void  setFile2(FileDescriptor file);

    @Property("office.common-files.file3")
    FileDescriptor getFile3();
    void  setFile3(FileDescriptor file);

    @Property("office.common-files.file4")
    FileDescriptor getFile4();
    void  setFile4(FileDescriptor file);

    @Property("office.common-files.file5")
    FileDescriptor getFile5();
    void  setFile5(FileDescriptor file);
}