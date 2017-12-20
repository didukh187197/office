alter table OFFICE_STEP_USER add constraint FK_OFFICE_STEP_USER_STEP foreign key (STEP_ID) references OFFICE_STEP(ID) on delete CASCADE;
alter table OFFICE_STEP_USER add constraint FK_OFFICE_STEP_USER_USER foreign key (USER_ID) references SEC_USER(ID);
create index IDX_OFFICE_STEP_USER_STEP on OFFICE_STEP_USER (STEP_ID);
create index IDX_OFFICE_STEP_USER_USER on OFFICE_STEP_USER (USER_ID);
