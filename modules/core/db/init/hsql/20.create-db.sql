-- begin OFFICE_STEP_ACTION
alter table OFFICE_STEP_ACTION add constraint FK_OFFICE_STEP_ACTION_STEP foreign key (STEP_ID) references OFFICE_STEP(ID) on delete CASCADE^
alter table OFFICE_STEP_ACTION add constraint FK_OFFICE_STEP_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID)^
create unique index IDX_OFFICE_STEP_ACTION_UNIQ_TEMPLATE_ID on OFFICE_STEP_ACTION (TEMPLATE_ID) ^
create index IDX_OFFICE_STEP_ACTION_STEP on OFFICE_STEP_ACTION (STEP_ID)^
-- end OFFICE_STEP_ACTION

-- begin OFFICE_REQUEST
alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_APPLICANT foreign key (APPLICANT_ID) references SEC_USER(ID)^
alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_STEP foreign key (STEP_ID) references OFFICE_STEP(ID)^
alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_USER foreign key (USER_ID) references SEC_USER(ID)^
create unique index IDX_OFFICE_REQUEST_UNIQ_APPLICANT_ID on OFFICE_REQUEST (APPLICANT_ID) ^
create unique index IDX_OFFICE_REQUEST_UNIQ_APPLICANT_CODE on OFFICE_REQUEST (APPLICANT_CODE) ^
create index IDX_OFFICE_REQUEST_STEP on OFFICE_REQUEST (STEP_ID)^
create index IDX_OFFICE_REQUEST_USER on OFFICE_REQUEST (USER_ID)^
-- end OFFICE_REQUEST

-- begin OFFICE_REQUEST_ACTION
alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE^
alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_STEP foreign key (STEP_ID) references OFFICE_STEP(ID)^
alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_USER foreign key (USER_ID) references SEC_USER(ID)^
alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID)^
alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_FILE foreign key (FILE_ID) references SYS_FILE(ID)^
create index IDX_OFFICE_REQUEST_ACTION_REQUEST on OFFICE_REQUEST_ACTION (REQUEST_ID)^
create index IDX_OFFICE_REQUEST_ACTION_STEP on OFFICE_REQUEST_ACTION (STEP_ID)^
create index IDX_OFFICE_REQUEST_ACTION_USER on OFFICE_REQUEST_ACTION (USER_ID)^
-- end OFFICE_REQUEST_ACTION

-- begin OFFICE_REQUEST_COMMUNICATION
alter table OFFICE_REQUEST_COMMUNICATION add constraint FK_OFFICE_REQUEST_COMMUNICATION_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE^
alter table OFFICE_REQUEST_COMMUNICATION add constraint FK_OFFICE_REQUEST_COMMUNICATION_FILE foreign key (FILE_ID) references SYS_FILE(ID)^
create index IDX_OFFICE_REQUEST_COMMUNICATION_REQUEST on OFFICE_REQUEST_COMMUNICATION (REQUEST_ID)^
-- end OFFICE_REQUEST_COMMUNICATION
-- begin OFFICE_STEP_USER
alter table OFFICE_STEP_USER add constraint FK_OFFICE_STEP_USER_STEP foreign key (STEP_ID) references OFFICE_STEP(ID) on delete CASCADE^
alter table OFFICE_STEP_USER add constraint FK_OFFICE_STEP_USER_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_OFFICE_STEP_USER_STEP on OFFICE_STEP_USER (STEP_ID)^
create index IDX_OFFICE_STEP_USER_USER on OFFICE_STEP_USER (USER_ID)^
-- end OFFICE_STEP_USER
-- begin OFFICE_REQUEST_STEP
alter table OFFICE_REQUEST_STEP add constraint FK_OFFICE_REQUEST_STEP_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE^
alter table OFFICE_REQUEST_STEP add constraint FK_OFFICE_REQUEST_STEP_STEP foreign key (STEP_ID) references OFFICE_STEP(ID)^
alter table OFFICE_REQUEST_STEP add constraint FK_OFFICE_REQUEST_STEP_USER foreign key (USER_ID) references SEC_USER(ID)^
create index IDX_OFFICE_REQUEST_STEP_REQUEST on OFFICE_REQUEST_STEP (REQUEST_ID)^
create index IDX_OFFICE_REQUEST_STEP_STEP on OFFICE_REQUEST_STEP (STEP_ID)^
create index IDX_OFFICE_REQUEST_STEP_USER on OFFICE_REQUEST_STEP (USER_ID)^
-- end OFFICE_REQUEST_STEP
