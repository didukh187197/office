alter table OFFICE_STEP_ACTION add column TEMPLATE_ID varchar(36) ;
alter table OFFICE_STEP_ACTION drop column WORK_DAYS cascade ;
