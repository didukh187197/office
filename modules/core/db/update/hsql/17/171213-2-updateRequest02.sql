alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_APPLICANT foreign key (APPLICANT_ID) references OFFICE_APPLICANT(ID);
