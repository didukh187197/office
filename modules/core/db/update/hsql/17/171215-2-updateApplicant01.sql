alter table OFFICE_APPLICANT add constraint FK_OFFICE_APPLICANT_REQUEST foreign key (REQUEST_ID) references OFFICE_REQUEST(ID);
