alter table OFFICE_REQUEST_STEP add column SUBMISSION_TERM date ;
alter table OFFICE_REQUEST_STEP add column APPROVAL_TERM date ;
alter table OFFICE_REQUEST_STEP drop column SUBMIT_TO cascade ;
alter table OFFICE_REQUEST_STEP drop column APPROVE_TO cascade ;
