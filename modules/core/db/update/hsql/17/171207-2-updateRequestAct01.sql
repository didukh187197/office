alter table OFFICE_REQUEST_ACT add constraint FK_OFFICE_REQUEST_ACT_ACT foreign key (ACT_ID) references OFFICE_STEP_ACT(ID);
