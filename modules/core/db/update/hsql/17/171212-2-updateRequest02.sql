alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_STEP foreign key (STEP_ID) references OFFICE_STEP(ID);
create index IDX_OFFICE_REQUEST_STEP on OFFICE_REQUEST (STEP_ID);
