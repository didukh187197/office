alter table OFFICE_REQUEST_ACTION add column UPDATE_TS timestamp ;
alter table OFFICE_REQUEST_ACTION add column UPDATED_BY varchar(50) ;
alter table OFFICE_REQUEST_ACTION drop column CREATED cascade ;
