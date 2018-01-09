alter table OFFICE_REQUEST_STEP_COMMUNICATION add constraint FK_OFFICE_REQUEST_STEP_COMMUNICATION_QUESTION_FILE foreign key (QUESTION_FILE_ID) references SYS_FILE(ID);
alter table OFFICE_REQUEST_STEP_COMMUNICATION add constraint FK_OFFICE_REQUEST_STEP_COMMUNICATION_ANSWER_FILE foreign key (ANSWER_FILE_ID) references SYS_FILE(ID);
alter table OFFICE_REQUEST_STEP_COMMUNICATION add constraint FK_OFFICE_REQUEST_STEP_COMMUNICATION_REQUEST_STEP foreign key (REQUEST_STEP_ID) references OFFICE_REQUEST_STEP(ID);
create index IDX_OFFICE_REQUEST_STEP_COMMUNICATION_REQUEST_STEP on OFFICE_REQUEST_STEP_COMMUNICATION (REQUEST_STEP_ID);
