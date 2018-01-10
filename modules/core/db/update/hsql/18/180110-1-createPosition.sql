create table OFFICE_POSITION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    IDENTIFIER integer,
    DESCRIPTION varchar(100),
    DAYS_FOR_SUBMISSION integer,
    DAYS_FOR_APPROVAL integer,
    --
    primary key (ID)
);
