create table OFFICE_REQUEST_LOG (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    INFO varchar(255),
    --
    primary key (ID)
);
