alter table OFFICE_REQUEST_STEP_COMMUNICATION add column CLOSED date ;
alter table OFFICE_REQUEST_STEP_COMMUNICATION drop column CONFIRMED cascade ;