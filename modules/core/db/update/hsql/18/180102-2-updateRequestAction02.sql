alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_OFFICE_REQUEST_ACTION_USER on OFFICE_REQUEST_ACTION (USER_ID);
