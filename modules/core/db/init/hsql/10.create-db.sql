-- begin OFFICE_REQUEST
create table OFFICE_REQUEST (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    SERIES varchar(10),
    NUMBER_ integer,
    STEP_ID varchar(36),
    USER_ID varchar(36),
    CREATED date,
    CLOSED date,
    DESCRIPTION varchar(100),
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST

-- begin OFFICE_REQUEST_STATUS
create table OFFICE_REQUEST_STATUS (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    DATE_ date not null,
    POSITION_ integer not null,
    USER_ID varchar(36),
    DESCRIPTION varchar(100),
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_STATUS

-- begin OFFICE_STEP
create table OFFICE_STEP (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    IDENTFIER integer,
    POSITION_ integer not null,
    USER_ID varchar(36),
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
    --
    REQUEST_ID varchar(36),
    TYPE_ varchar(50),
    DESCRIPTION varchar(100),
    DEADLINE date,
    CREATED date,
    SUBMITTED date,
    APPROVED date,
    TEMPLATE_ID varchar(36),
    FILE_ID varchar(36),
    MESSAGE varchar(255),
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_ACTION
-- begin OFFICE_REQUEST_COMMUNICATION
create table OFFICE_REQUEST_COMMUNICATION (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    REQUEST_ID varchar(36),
    QUESTION varchar(255),
    ANSWER varchar(255),
    FILE_ID varchar(36),
    --
    primary key (ID)
)^
-- end OFFICE_REQUEST_COMMUNICATION
-- begin OFFICE_APPLICANT
create table OFFICE_APPLICANT (
    ID varchar(36) not null,
    CREATE_TS timestamp,
    CREATED_BY varchar(50),
    --
    USER_ID varchar(36),
    REQUEST_ID varchar(36),
    CODE varchar(15),
    --
    primary key (ID)
)^
-- end OFFICE_APPLICANT
