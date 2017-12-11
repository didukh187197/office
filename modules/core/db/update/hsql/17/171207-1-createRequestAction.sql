create table OFFICE_REQUEST_ACTION (
    ID varchar(36) not null,
    VERSION integer not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    DELETE_TS timestamp,
    DELETED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    ACT_ID varchar(36),
    DESCRIPTION varchar(100),
    CREATED date,
    DEADLINE date,
    CLOSED date,
    RESULT_ varchar(255),
    --
    primary key (ID)
);
