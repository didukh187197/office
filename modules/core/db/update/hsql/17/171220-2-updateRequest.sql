alter table OFFICE_REQUEST add column UPDATE_TS timestamp ;
alter table OFFICE_REQUEST add column UPDATED_BY varchar(50) ;
alter table OFFICE_REQUEST drop column CREATED cascade ;
