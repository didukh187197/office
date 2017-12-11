alter table SEC_USER add constraint FK_SEC_USER_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID);
