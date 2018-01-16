-- begin OFFICE_REQUEST
alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_APPLICANT foreign key (APPLICANT_ID) references SEC_USER(ID)^
alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_STEP foreign key (STEP_ID) references OFFICE_REQUEST_STEP(ID)^
create unique index IDX_OFFICE_REQUEST_UNIQ_APPLICANT_ID on OFFICE_REQUEST (APPLICANT_ID) ^
create unique index IDX_OFFICE_REQUEST_UNIQ_APPLICANT_CODE on OFFICE_REQUEST (APPLICANT_CODE) ^
-- end OFFICE_REQUEST

-- begin OFFICE_REQUEST_STEP
alter table OFFICE_REQUEST_STEP add constraint FK_OFFICE_REQUEST_STEP_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE^
alter table OFFICE_REQUEST_STEP add constraint FK_OFFICE_REQUEST_STEP_POSITION foreign key (POSITION_ID) references OFFICE_POSITION(ID)^
alter table OFFICE_REQUEST_STEP add constraint FK_OFFICE_REQUEST_STEP_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_OFFICE_REQUEST_STEP_REQUEST on OFFICE_REQUEST_STEP (REQUEST_ID)^
create index IDX_OFFICE_REQUEST_STEP_POSITION on OFFICE_REQUEST_STEP (POSITION_ID)^
create index IDX_OFFICE_REQUEST_STEP_USER on OFFICE_REQUEST_STEP (USER_ID)^
-- end OFFICE_REQUEST_STEP
-- begin OFFICE_REQUEST_STEP_ACTION
alter table OFFICE_REQUEST_STEP_ACTION add constraint FK_OFFICE_REQUEST_STEP_ACTION_REQUEST_STEP foreign key (REQUEST_STEP_ID) references OFFICE_REQUEST_STEP(ID) on delete CASCADE^
alter table OFFICE_REQUEST_STEP_ACTION add constraint FK_OFFICE_REQUEST_STEP_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID)^
alter table OFFICE_REQUEST_STEP_ACTION add constraint FK_OFFICE_REQUEST_STEP_ACTION_FILE foreign key (FILE_ID) references SYS_FILE(ID)^
create index IDX_OFFICE_REQUEST_STEP_ACTION_REQUEST_STEP on OFFICE_REQUEST_STEP_ACTION (REQUEST_STEP_ID)^
-- end OFFICE_REQUEST_STEP_ACTION
-- begin OFFICE_REQUEST_LOG
alter table OFFICE_REQUEST_LOG add constraint FK_OFFICE_REQUEST_LOG_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE^
alter table OFFICE_REQUEST_LOG add constraint FK_OFFICE_REQUEST_LOG_SENDER foreign key (SENDER_ID) references SEC_USER(ID)^
alter table OFFICE_REQUEST_LOG add constraint FK_OFFICE_REQUEST_LOG_RECEPIENT foreign key (RECEPIENT_ID) references SEC_USER(ID)^
create index IDX_OFFICE_REQUEST_LOG_REQUEST on OFFICE_REQUEST_LOG (REQUEST_ID)^
create index IDX_OFFICE_REQUEST_LOG_SENDER on OFFICE_REQUEST_LOG (SENDER_ID)^
create index IDX_OFFICE_REQUEST_LOG_RECEPIENT on OFFICE_REQUEST_LOG (RECEPIENT_ID)^
-- end OFFICE_REQUEST_LOG
-- begin OFFICE_REQUEST_STEP_COMMUNICATION
alter table OFFICE_REQUEST_STEP_COMMUNICATION add constraint FK_OFFICE_REQUEST_STEP_COMMUNICATION_REQUEST_STEP foreign key (REQUEST_STEP_ID) references OFFICE_REQUEST_STEP(ID) on delete CASCADE^
alter table OFFICE_REQUEST_STEP_COMMUNICATION add constraint FK_OFFICE_REQUEST_STEP_COMMUNICATION_QUESTION_FILE foreign key (QUESTION_FILE_ID) references SYS_FILE(ID)^
alter table OFFICE_REQUEST_STEP_COMMUNICATION add constraint FK_OFFICE_REQUEST_STEP_COMMUNICATION_ANSWER_FILE foreign key (ANSWER_FILE_ID) references SYS_FILE(ID)^
create index IDX_OFFICE_REQUEST_STEP_COMMUNICATION_REQUEST_STEP on OFFICE_REQUEST_STEP_COMMUNICATION (REQUEST_STEP_ID)^
-- end OFFICE_REQUEST_STEP_COMMUNICATION
-- begin OFFICE_POSITION_ACTION
alter table OFFICE_POSITION_ACTION add constraint FK_OFFICE_POSITION_ACTION_POSITION foreign key (POSITION_ID) references OFFICE_POSITION(ID) on delete CASCADE^
alter table OFFICE_POSITION_ACTION add constraint FK_OFFICE_POSITION_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID)^
create unique index IDX_OFFICE_POSITION_ACTION_UNIQ_TEMPLATE_ID on OFFICE_POSITION_ACTION (TEMPLATE_ID) ^
create index IDX_OFFICE_POSITION_ACTION_POSITION on OFFICE_POSITION_ACTION (POSITION_ID)^
-- end OFFICE_POSITION_ACTION
-- begin OFFICE_POSITION_USER
alter table OFFICE_POSITION_USER add constraint FK_OFFICE_POSITION_USER_POSITION foreign key (POSITION_ID) references OFFICE_POSITION(ID) on delete CASCADE^
alter table OFFICE_POSITION_USER add constraint FK_OFFICE_POSITION_USER_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_OFFICE_POSITION_USER_POSITION on OFFICE_POSITION_USER (POSITION_ID)^
create index IDX_OFFICE_POSITION_USER_USER on OFFICE_POSITION_USER (USER_ID)^
-- end OFFICE_POSITION_USER
