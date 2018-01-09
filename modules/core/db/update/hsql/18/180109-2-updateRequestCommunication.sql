alter table OFFICE_REQUEST_COMMUNICATION add column QUESTION_FILE_ID varchar(36) ;
alter table OFFICE_REQUEST_COMMUNICATION add column ANSWER_FILE_ID varchar(36) ;
alter table OFFICE_REQUEST_COMMUNICATION drop column FILE_ID cascade ;
