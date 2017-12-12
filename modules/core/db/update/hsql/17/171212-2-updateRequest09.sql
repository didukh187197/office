alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_WORKER foreign key (WORKER_ID) references SEC_USER(ID);
