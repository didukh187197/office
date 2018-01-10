create table OFFICE_POSITION_USER (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    POSITION_ID varchar(36),
    USER_ID varchar(36),
    REQUESTS integer,
    THRESHOLD integer,
    --
    primary key (ID)
);
