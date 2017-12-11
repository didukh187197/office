create table OFFICE_STEP_ACT (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    STEP_ID varchar(36),
    TYPE_ varchar(50) not null,
    POSITION_ integer not null,
    DESCRIPTION varchar(100),
    WORK_DAYS integer not null,
    RESULT_REQUIRED boolean,
    --
    primary key (ID)
);
