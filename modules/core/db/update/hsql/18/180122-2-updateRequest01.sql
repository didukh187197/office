alter table OFFICE_REQUEST add constraint FK_OFFICE_REQUEST_IMAGE_FILE foreign key (IMAGE_FILE_ID) references SYS_FILE(ID);
