alter table OFFICE_REQUEST_COMMUNICATION add constraint FK_OFFICE_REQUEST_COMMUNICATION_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE;
