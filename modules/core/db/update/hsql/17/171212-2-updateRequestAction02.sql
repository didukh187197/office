alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_FILE foreign key (FILE_ID) references SYS_FILE(ID);
