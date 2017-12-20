alter table OFFICE_REQUEST_STATUS add column STEP_ID varchar(36) ;
alter table OFFICE_REQUEST_STATUS drop column POSITION_ cascade ;
