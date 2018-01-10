alter table OFFICE_REQUEST_STEP add column POSITION_ID varchar(36) ;
alter table OFFICE_REQUEST_STEP drop column STEP_ID cascade ;
