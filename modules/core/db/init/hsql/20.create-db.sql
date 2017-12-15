-- begin OFFICE_APPLICANT
alter table OFFICE_APPLICANT add constraint FK_OFFICE_APPLICANT_USER foreign key (USER_ID) references SEC_USER(ID)^
alter table OFFICE_APPLICANT add constraint FK_OFFICE_APPLICANT_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID)^
create unique index IDX_OFFICE_APPLICANT_UNIQ_CODE on OFFICE_APPLICANT (CODE) ^
create unique index IDX_OFFICE_APPLICANT_UNIQ_USER_ID on OFFICE_APPLICANT (USER_ID) ^
-- end OFFICE_APPLICANT

-- begin OFFICE_STEP
alter table OFFICE_STEP add constraint FK_OFFICE_STEP_USER foreign key (USER_ID) references SEC_USER(ID)^
-- end OFFICE_STEP

-- begin OFFICE_STEP_ACTION
alter table OFFICE_STEP_ACTION add constraint FK_OFFICE_STEP_ACTION_STEP foreign key (STEP_ID) references OFFICE_STEP(ID)^
alter table OFFICE_STEP_ACTION add constraint FK_OFFICE_STEP_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID)^
create index IDX_OFFICE_STEP_ACTION_STEP on OFFICE_STEP_ACTION (STEP_ID)^
-- end OFFICE_STEP_ACTION

-- begin OFFICE_REQUEST
alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_STEP foreign key (STEP_ID) references OFFICE_STEP(ID)^
alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_OFFICE_REQUEST_STEP on OFFICE_REQUEST (STEP_ID)^
create index IDX_OFFICE_REQUEST_USER on OFFICE_REQUEST (USER_ID)^
-- end OFFICE_REQUEST

-- begin OFFICE_REQUEST_ACTION
alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID)^
alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID)^
alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_FILE foreign key (FILE_ID) references SYS_FILE(ID)^
create index IDX_OFFICE_REQUEST_ACTION_REQUEST on OFFICE_REQUEST_ACTION (REQUEST_ID)^
-- end OFFICE_REQUEST_ACTION

-- begin OFFICE_REQUEST_STATUS
alter table OFFICE_REQUEST_STATUS add constraint FK_OFFICE_REQUEST_STATUS_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID)^
alter table OFFICE_REQUEST_STATUS add constraint FK_OFFICE_REQUEST_STATUS_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_OFFICE_REQUEST_STATUS_REQUEST on OFFICE_REQUEST_STATUS (REQUEST_ID)^
create index IDX_OFFICE_REQUEST_STATUS_USER on OFFICE_REQUEST_STATUS (USER_ID)^
-- end OFFICE_REQUEST_STATUS

-- begin OFFICE_REQUEST_COMMUNICATION
alter table OFFICE_REQUEST_COMMUNICATION add constraint FK_OFFICE_REQUEST_COMMUNICATION_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID)^
alter table OFFICE_REQUEST_COMMUNICATION add constraint FK_OFFICE_REQUEST_COMMUNICATION_FILE foreign key (FILE_ID) references SYS_FILE(ID)^
create index IDX_OFFICE_REQUEST_COMMUNICATION_REQUEST on OFFICE_REQUEST_COMMUNICATION (REQUEST_ID)^
-- end OFFICE_REQUEST_COMMUNICATION
