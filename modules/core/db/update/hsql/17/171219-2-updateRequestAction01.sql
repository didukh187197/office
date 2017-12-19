alter table OFFICE_REQUEST_ACTION add constraint FK_OFFICE_REQUEST_ACTION_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE;
