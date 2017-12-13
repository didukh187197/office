alter table OFFICE_REQUEST drop column APPLICANT_ID cascade ;
alter table OFFICE_REQUEST alter column SERIES set null ;
alter table OFFICE_REQUEST alter column NUMBER_ set null ;
