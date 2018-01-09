alter table OFFICE_REQUEST drop column STEP_ID cascade ;
alter table OFFICE_REQUEST add column STEP_ID varchar(36) ;
alter table OFFICE_REQUEST drop column USER_ID cascade ;
alter table OFFICE_REQUEST drop column STATE cascade ;
alter table OFFICE_REQUEST drop column PENALTY cascade ;
