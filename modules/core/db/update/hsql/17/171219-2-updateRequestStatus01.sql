alter table OFFICE_REQUEST_STATUS add constraint FK_OFFICE_REQUEST_STATUS_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE;
