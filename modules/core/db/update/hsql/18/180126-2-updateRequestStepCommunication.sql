alter table OFFICE_REQUEST_STEP_COMMUNICATION add column CONFIRMED date ;
alter table OFFICE_REQUEST_STEP_COMMUNICATION drop column READ_ cascade ;
