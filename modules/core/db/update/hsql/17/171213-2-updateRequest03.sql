alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_OFFICE_REQUEST_USER on OFFICE_REQUEST (USER_ID);
