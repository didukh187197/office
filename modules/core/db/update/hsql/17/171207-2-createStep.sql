alter table OFFICE_STEP add constraint FK_OFFICE_STEP_USER foreign key (USER_ID) references SEC_USER(ID);
