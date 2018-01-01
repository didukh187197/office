alter table OFFICE_REQUEST_STEP add constraint FK_OFFICE_REQUEST_STEP_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID) on delete CASCADE;
