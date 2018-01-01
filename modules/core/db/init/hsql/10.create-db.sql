-- begin OFFICE_REQUEST
create table OFFICE_REQUEST (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    APPLICANT_ID varchar(36),
    APPLICANT_CODE varchar(15),
    SERIES varchar(10),
    NUMBER_ integer,
    DESCRIPTION varchar(100),
    STEP_ID varchar(36),
    USER_ID varchar(36),
    STATE varchar(50),
    PENALTY integer,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST

-- begin OFFICE_STEP
create table OFFICE_STEP (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    IDENTIFIER integer,
    DESCRIPTION varchar(100),
    --
    primary key (ID)
)^
-- end OFFICE_STEP
-- begin OFFICE_STEP_ACTION
create table OFFICE_STEP_ACTION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    STEP_ID varchar(36),
    TYPE_ varchar(50) not null,
    WORK_DAYS integer,
    DESCRIPTION varchar(100),
    TEMPLATE_ID varchar(36),
    --
    primary key (ID)
)^
-- end OFFICE_STEP_ACTION
-- begin OFFICE_REQUEST_ACTION
create table OFFICE_REQUEST_ACTION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    TYPE_ varchar(50),
    DESCRIPTION varchar(100),
    DEADLINE date,
    SUBMITTED date,
    APPROVED date,
    TEMPLATE_ID varchar(36),
    FILE_ID varchar(36),
    MESSAGE varchar(255),
    PENALTY integer,
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_ACTION
-- begin OFFICE_REQUEST_COMMUNICATION
create table OFFICE_REQUEST_COMMUNICATION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    QUESTION varchar(255),
    ANSWER varchar(255),
    FILE_ID varchar(36),
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_COMMUNICATION

-- begin OFFICE_STEP_USER
create table OFFICE_STEP_USER (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    --
    STEP_ID varchar(36),
    USER_ID varchar(36),
    REQUESTS integer,
    THRESHOLD integer,
    --
    primary key (ID)
)^
-- end OFFICE_STEP_USER
-- begin OFFICE_REQUEST_STEP
create table OFFICE_REQUEST_STEP (
    ID varchar(36) not null,
    UPDATE_TS timestamp,
    UPDATED_BY varchar(50),
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    STEP_ID varchar(36),
    USER_ID varchar(36),
    DESCRIPTION varchar(100),
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_STEP
