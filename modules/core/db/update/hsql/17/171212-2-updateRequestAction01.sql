alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_TEMPLATE foreign key (TEMPLATE_ID) references SYS_FILE(ID);
