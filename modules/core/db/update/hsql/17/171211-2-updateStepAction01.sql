alter table OFFICE_STEP_ACTION add constraint FK_OFFICE_STEP_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID);
