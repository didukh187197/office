alter table OFFICE_REQUEST_STATUS add column UPDATE_TS timestamp ;
alter table OFFICE_REQUEST_STATUS add column UPDATED_BY varchar(50) ;
alter table OFFICE_REQUEST_STATUS drop column DATE_ cascade ;
