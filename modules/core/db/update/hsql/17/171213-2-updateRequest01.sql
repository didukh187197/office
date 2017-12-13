alter table OFFICE_REQUEST add column APPLICANT_ID varchar(36) ;
alter table OFFICE_REQUEST add column USER_ID varchar(36) ;
alter table OFFICE_REQUEST drop column WORKER_ID cascade ;
