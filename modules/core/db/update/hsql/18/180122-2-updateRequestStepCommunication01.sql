alter table OFFICE_REQUEST_STEP_COMMUNICATION add constraint FK_OFFICE_REQUEST_STEP_COMMUNICATION_INITIATOR foreign key (INITIATOR_ID) references SEC_USER(ID);
create index IDX_OFFICE_REQUEST_STEP_COMMUNICATION_INITIATOR on OFFICE_REQUEST_STEP_COMMUNICATION (INITIATOR_ID);
