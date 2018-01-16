alter table OFFICE_REQUEST_LOG add column MOMENT bigint ;
alter table OFFICE_REQUEST_LOG drop column READ_ cascade ;
alter table OFFICE_REQUEST_LOG add column READ_ date ;
