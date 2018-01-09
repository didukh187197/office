alter table OFFICE_REQUEST_STEP_ACTION add constraint FK_OFFICE_REQUEST_STEP_ACTION_REQUEST_STEP foreign key (REQUEST_STEP_ID) references OFFICE_REQUEST_STEP(ID) on delete CASCADE;
alter table OFFICE_REQUEST_STEP_ACTION add constraint FK_OFFICE_REQUEST_STEP_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID);
alter table OFFICE_REQUEST_STEP_ACTION add constraint FK_OFFICE_REQUEST_STEP_ACTION_FILE foreign key (FILE_ID) references SYS_FILE(ID);
create index IDX_OFFICE_REQUEST_STEP_ACTION_REQUEST_STEP on OFFICE_REQUEST_STEP_ACTION (REQUEST_STEP_ID);
