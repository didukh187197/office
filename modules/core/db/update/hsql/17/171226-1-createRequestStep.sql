create table OFFICE_REQUEST_STEP (
    ID varchar(36) not null,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    STEP_ID varchar(36),
    USER_ID varchar(36),
    DESCRIPTION varchar(100),
    REQUEST_ID varchar(36),
    --
    primary key (ID)
);
