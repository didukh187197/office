alter table OFFICE_REQUEST add column SERIES varchar(10) ^
update OFFICE_REQUEST set SERIES = '' where SERIES is null ;
alter table OFFICE_REQUEST alter column SERIES set not null ;
-- alter table OFFICE_REQUEST add column NUMBER_ integer ^
-- update OFFICE_REQUEST set NUMBER_ = <default_value> ;
-- alter table OFFICE_REQUEST alter column NUMBER_ set not null ;
alter table OFFICE_REQUEST add column NUMBER_ integer ;
