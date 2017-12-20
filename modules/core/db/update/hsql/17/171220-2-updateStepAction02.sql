alter table OFFICE_STEP_ACTION add constraint FK_OFFICE_STEP_ACTION_STEP foreign key (STEP_ID) references OFFICE_STEP(ID) on delete CASCADE;
