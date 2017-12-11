alter table OFFICE_REQUEST_ACT drop column ACT_ID cascade ;
alter table OFFICE_REQUEST_ACT add column ACT_ID varchar(36) ;
