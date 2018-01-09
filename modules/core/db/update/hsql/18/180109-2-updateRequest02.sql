alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_STEP foreign key (STEP_ID) references OFFICE_REQUEST_STEP(ID);
