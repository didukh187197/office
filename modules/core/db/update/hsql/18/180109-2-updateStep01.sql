alter table OFFICE_STEP add column DAYS_FOR_SUBMISSION integer ;
alter table OFFICE_STEP add column DAYS_FOR_APPROVAL integer ;
alter table OFFICE_STEP drop column DAYS_FOR_SUBMIT cascade ;
alter table OFFICE_STEP drop column DAYS_FOR_APPROVE cascade ;
