alter table OFFICE_REQUEST add column STEP_ID varchar(36) ;
alter table OFFICE_REQUEST drop column POSITION_ cascade ;
