alter table OFFICE_REQUEST_STEP_COMMUNICATION add constraint FK_OFFICE_REQUEST_STEP_COMMUNICATION_RECEPIENT foreign key (RECEPIENT_ID) references SEC_USER(ID);
create index IDX_OFFICE_REQUEST_STEP_COMMUNICATION_RECEPIENT on OFFICE_REQUEST_STEP_COMMUNICATION (RECEPIENT_ID);
