alter table OFFICE_STEP_ACT add constraint FK_OFFICE_STEP_ACT_STEP foreign key (STEP_ID) references OFFICE_STEP(ID);
create index IDX_OFFICE_STEP_ACT_STEP on OFFICE_STEP_ACT (STEP_ID);
