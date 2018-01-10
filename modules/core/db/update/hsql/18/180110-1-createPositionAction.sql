create table OFFICE_POSITION_ACTION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    POSITION_ID varchar(36),
    TYPE_ varchar(50) not null,
    DESCRIPTION varchar(100),
    TEMPLATE_ID varchar(36),
    --
    primary key (ID)
);
