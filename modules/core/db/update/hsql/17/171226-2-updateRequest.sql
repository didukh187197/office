alter table OFFICE_REQUEST add column STATE varchar(50) ;
alter table OFFICE_REQUEST drop column CLOSED cascade ;
