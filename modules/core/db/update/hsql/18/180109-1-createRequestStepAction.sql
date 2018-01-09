create table OFFICE_REQUEST_STEP_ACTION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    REQUEST_STEP_ID varchar(36),
    TYPE_ varchar(50),
    DESCRIPTION varchar(100),
    TEMPLATE_ID varchar(36),
    FILE_ID varchar(36),
    MESSAGE varchar(255),
    SUBMITTED date,
    APPROVED date,
    --
    primary key (ID)
);
