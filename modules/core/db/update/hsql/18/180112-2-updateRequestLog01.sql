alter table OFFICE_REQUEST_LOG add constraint FK_OFFICE_REQUEST_LOG_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE;
